package com.ia.agentsia.services.impl;

import java.util.function.Function;

// Definir una función con 2 arg. Uno será un Request y otro un Response
// Function = interfaz funcional de Java, recibe un tipo de dato como entrada <T>.
// y devuelve otro tipo de dato como salida <R>

public class MockWeatherService implements Function<MockWeatherService.Request, MockWeatherService.Response> {

    public enum Unit {
        C, // C = Celsius
        F, // F = Fahrenheit
    }

    // Representa la información que necesita nuestro servicio para realizar la
    // consulta
    // record: Java genera automáticamente sus getters, setters y constructor,
    // toString...
    public record Request(String location, Unit unit) {

    }

    public record Response(double temp, Unit unit) {

    }

    @Override
    public Response apply(Request request) {

        return new Response(30.0, Unit.C);

    }

}
