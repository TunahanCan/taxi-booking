package com.taxibooking.eventstreamer.models;


import com.taxibooking.eventstreamer.models.enums.CommandType;

public record BrokerRequest(CommandType command, String topic, long offset, String payload) { }