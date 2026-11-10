package com.faculdade.sensor;

public abstract class SensorMonitorado extends Componente {

    private final double limiteAlerta;
    private final double limiteCritico;
    private Nivel nivel = Nivel.NORMAL;

    protected SensorMonitorado(String nome, double limiteAlerta, double limiteCritico) {
        super(nome);
        if (!(limiteAlerta < limiteCritico)) {
            throw new IllegalArgumentException("o limite de alerta deve ser menor que o crítico");
        }
        this.limiteAlerta = limiteAlerta;
        this.limiteCritico = limiteCritico;
    }

    public void ler(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("leitura inválida: " + valor);
        }
        Nivel novo = classificar(valor);
        if (novo != nivel) {
            notificarMediador(eventoPara(novo));
            nivel = novo;
        }
    }

    public Nivel getNivel() {
        return nivel;
    }

    private Nivel classificar(double valor) {
        if (valor >= limiteCritico) return Nivel.CRITICO;
        if (valor >= limiteAlerta) return Nivel.ALERTA;
        return Nivel.NORMAL;
    }

    protected abstract Evento eventoPara(Nivel nivel);
}
