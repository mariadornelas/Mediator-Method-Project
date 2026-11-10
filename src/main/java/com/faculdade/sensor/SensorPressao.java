package com.faculdade.sensor;

/** Colega: pressão em bar (alerta a partir de 8, crítico a partir de 10). */
public class SensorPressao extends SensorMonitorado {

    public SensorPressao(String nome) {
        super(nome, 8.0, 10.0);
    }

    @Override
    protected Evento eventoPara(Nivel nivel) {
        return switch (nivel) {
            case NORMAL -> Evento.PRESSAO_NORMALIZADA;
            case ALERTA -> Evento.PRESSAO_ALTA;
            case CRITICO -> Evento.PRESSAO_CRITICA;
        };
    }
}
