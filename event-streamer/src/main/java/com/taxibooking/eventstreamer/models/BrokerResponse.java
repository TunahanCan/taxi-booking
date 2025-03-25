package com.taxibooking.eventstreamer.models;

public record BrokerResponse(int statusCode, String topic, long offset, String payload) { }
