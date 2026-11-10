package com.faculdade.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Colega que fala nos dois sentidos: exibe mensagens (recebe comandos do mediador)
 * e permite ao operador pedir o silenciamento do alarme (envia evento ao mediador).
 */
public class Painel extends Componente {

    private final List<String> mensagens = new ArrayList<>();

    public Painel(String nome) {
        super(nome);
    }

    public void exibir(String mensagem) {
        mensagens.add(Objects.requireNonNull(mensagem, "mensagem não pode ser nula"));
    }

    /** Ação do operador: o painel não decide se o pedido é aceito, só o comunica. */
    public void solicitarSilenciamento() {
        notificarMediador(Evento.SILENCIAR_ALARME);
    }

    public List<String> getMensagens() {
        return Collections.unmodifiableList(mensagens);
    }
}
