package com.faculdade.sensor;

/**
 * Mediator: a única coisa que os componentes conhecem do resto do sistema.
 * Interface funcional, então um mediador alternativo (ou um espião de teste)
 * pode ser uma lambda.
 */
@FunctionalInterface
public interface Mediador {

    void notificar(Componente origem, Evento evento);
}
