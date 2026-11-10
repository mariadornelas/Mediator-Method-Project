package com.faculdade.sensor;

public enum Evento {
    TEMPERATURA_ALTA("temperatura em alerta"),
    TEMPERATURA_CRITICA("temperatura crítica"),
    TEMPERATURA_NORMALIZADA("temperatura normalizada"),
    PRESSAO_ALTA("pressão em alerta"),
    PRESSAO_CRITICA("pressão crítica"),
    PRESSAO_NORMALIZADA("pressão normalizada"),
    SILENCIAR_ALARME("pedido de silenciamento do alarme");

    private final String descricao;

    Evento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
