package com.faculdade.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Painel extends Componente {

    private final List<String> mensagens = new ArrayList<>();

    public Painel(String nome) {
        super(nome);
    }

    public void exibir(String mensagem) {
        mensagens.add(Objects.requireNonNull(mensagem, "mensagem não pode ser nula"));
    }

    public void solicitarSilenciamento() {
        notificarMediador(Evento.SILENCIAR_ALARME);
    }

    public List<String> getMensagens() {
        return Collections.unmodifiableList(mensagens);
    }
}
