package com.faculdade.sensor;

import java.util.Objects;

/**
 * Colleague (colega): base de todo componente da sala. Só guarda uma referência
 * ao {@link Mediador}, nunca a outro componente. É assim que os colegas
 * ficam desacoplados entre si.
 */
public abstract class Componente {

    private final String nome;
    private Mediador mediador;

    protected Componente(String nome) {
        Objects.requireNonNull(nome, "nome não pode ser nulo");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("nome não pode ser vazio");
        }
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void definirMediador(Mediador mediador) {
        this.mediador = Objects.requireNonNull(mediador, "mediador não pode ser nulo");
    }

    /** Avisa o mediador de que algo aconteceu neste componente. */
    protected final void notificarMediador(Evento evento) {
        if (mediador == null) {
            throw new IllegalStateException(nome + " não está conectado a um mediador");
        }
        mediador.notificar(this, evento);
    }
}
