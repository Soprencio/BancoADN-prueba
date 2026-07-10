package com.mycompany.bancoadn.cliente.httpapi.bridge;

import java.util.HashMap;
import java.util.Map;

public class BridgeResult {
    private final Map<String, Object> data = new HashMap<>();

    public void put(String key, Object value) { data.put(key, value); }
    public Object get(String key) { return data.get(key); }
    public Map<String, Object> toMap() { return new HashMap<>(data); }

    public void setSuccess(boolean success) { data.put("success", success); }
    public void setMessage(String message) { data.put("message", message); }
    public void setError(String error) { data.put("success", false); data.put("error", error); }
    public void setNavigate(String view) { data.put("navigate", view); }
    public void setRedirect(String path) { data.put("redirect", path); }
    public void addCard(String type, Map<String, Object> cardData) {
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> cards = (java.util.List<Map<String, Object>>) data.computeIfAbsent(type, k -> new java.util.ArrayList<>());
        cards.add(cardData);
    }
}
