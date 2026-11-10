package com.faculdade.sensor;

@FunctionalInterface
public interface Mediador {

    void notificar(Componente origem, Evento evento);
}
