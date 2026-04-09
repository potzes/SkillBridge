package com.florence.skillbridge.Util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

@ApplicationScoped
public class JsonbProvider {
    private static final Jsonb jsonb = JsonbBuilder.create();

    public Jsonb getJsonb() {
        return jsonb;
    }
}
