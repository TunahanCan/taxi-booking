package com.taxibooking.eventstreamer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class EventCoordinator {

    private static final int COORDINATOR_PORT = 2181;
    private final String instanceId;
    private final Map<String, InstanceInfo> instances = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, String>> topicPartitionAssignments = new ConcurrentHashMap<>();
    private String leaderInstanceId;
    private boolean isCoordinatorNode;
    private Socket coordinatorConnection;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public EventCoordinator(boolean isCoordinator) {
        this.instanceId = UUID.randomUUID().toString();
        this.isCoordinatorNode = isCoordinator;

        if (isCoordinator) {
            startCoordinatorServer();
        } else {
            connectToCoordinator();
        }

        // Periyodik heartbeat ve partition balance kontrolü
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, 0, 5, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::rebalanceIfNeeded, 10, 30, TimeUnit.SECONDS);
    }

    private void startCoordinatorServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(COORDINATOR_PORT)) {
                System.out.println("Coordinator server started on port " + COORDINATOR_PORT);

                // Kendimizi lider olarak kaydet
                leaderInstanceId = instanceId;
                instances.put(instanceId, new InstanceInfo(instanceId, "localhost", 39092, true));

                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClientConnection(clientSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClientConnection(Socket clientSocket) {
        // İstemci bağlantısını işle - burada komutları işleyecek basit bir protokol olabilir
        // Örneğin: REGISTER,id,host,port
        //          HEARTBEAT,id
        //          GET_ASSIGNMENTS
        //          GET_LEADER
    }

    private void connectToCoordinator() {
        try {
            coordinatorConnection = new Socket("localhost", COORDINATOR_PORT);
            // Kendimizi kaydet
            String registerCommand = "REGISTER," + instanceId + ",localhost," + 39092;
            coordinatorConnection.getOutputStream().write(registerCommand.getBytes());

            // Cevapları dinle
            new Thread(this::listenForCoordinatorMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
            // Bağlantı başarısız olursa, belki kendimiz koordinatör olabiliriz
            scheduleCoordinatorElection();
        }
    }

    private void listenForCoordinatorMessages() {
        // Koordinatörden gelen mesajları dinle
    }

    private void scheduleCoordinatorElection() {
        // Basit bir seçim algoritması
        // Gerçek dağıtık sistemlerde bu daha karmaşık olacaktır
        scheduler.schedule(() -> {
            // Başka bir koordinatör var mı kontrol et, yoksa kendimiz olalım
            try (Socket testConnection = new Socket("localhost", COORDINATOR_PORT)) {
                // Bağlantı başarılı, başka bir koordinatör var
            } catch (IOException e) {
                // Koordinatör yok, ben koordinatör oluyorum
                isCoordinatorNode = true;
                startCoordinatorServer();
            }
        }, 5, TimeUnit.SECONDS);
    }

    private void sendHeartbeat() {
        if (isCoordinatorNode) return;
        try {
            if (coordinatorConnection == null || coordinatorConnection.isClosed())
            {
                connectToCoordinator();
            }
            String heartbeatCommand = "HEARTBEAT," + instanceId;
            coordinatorConnection.getOutputStream().write(heartbeatCommand.getBytes());
        } catch (IOException e) {
            // Koordinatör bağlantısı kopmuş
            scheduleCoordinatorElection();
        }
    }

    private void rebalanceIfNeeded() {
        if (!isCoordinatorNode) return;

        // Aktif instance'ları kontrol et
        Set<String> activeInstances = instances.entrySet().stream()
                .filter(e -> e.getValue().isActive())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (activeInstances.isEmpty()) return;

        // Her topic için partitionları aktif instance'lara dağıt
        for (Map.Entry<String, Map<Integer, String>> topicEntry : topicPartitionAssignments.entrySet()) {
            String topic = topicEntry.getKey();
            Map<Integer, String> assignments = topicEntry.getValue();

            // Her partition için bir instance seç
            for (int partitionId : assignments.keySet()) {
                String currentAssignee = assignments.get(partitionId);

                // Eğer atanan instance aktif değilse, yeniden ata
                if (!activeInstances.contains(currentAssignee)) {
                    // Basit round-robin seçimi
                    String newAssignee = activeInstances.iterator().next();
                    assignments.put(partitionId, newAssignee);

                    // Değişikliği tüm instance'lara bildir
                    notifyPartitionReassignment(topic, partitionId, newAssignee);
                }
            }
        }
    }

    private void notifyPartitionReassignment(String topic, int partitionId, String newAssignee) {
        // Tüm instance'lara bildirim gönder
    }

    public boolean isResponsibleForTopicPartition(String topic, int partition) {
        if (!topicPartitionAssignments.containsKey(topic)) {
            if (isCoordinatorNode) {
                Map<Integer, String> partitionMap = new ConcurrentHashMap<>();
                partitionMap.put(partition, instanceId);
                topicPartitionAssignments.put(topic, partitionMap);
                return true;
            }
            return false;
        }
        Map<Integer, String> assignments = topicPartitionAssignments.get(topic);
        return instanceId.equals(assignments.getOrDefault(partition, leaderInstanceId));
    }

    private static class InstanceInfo {
        private final String id;
        private final String host;
        private final int port;
        private boolean active;
        private long lastHeartbeat;

        public InstanceInfo(String id, String host, int port, boolean active) {
            this.id = id;
            this.host = host;
            this.port = port;
            this.active = active;
            this.lastHeartbeat = System.currentTimeMillis();
        }

        public boolean isActive() {
            return active && (System.currentTimeMillis() - lastHeartbeat) < 15000; // 15 saniye timeout
        }

        public void updateHeartbeat() {
            lastHeartbeat = System.currentTimeMillis();
            active = true;
        }
    }


}
