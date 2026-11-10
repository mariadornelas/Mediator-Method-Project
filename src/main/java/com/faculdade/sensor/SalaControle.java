package com.faculdade.sensor;

import java.util.Objects;

public class SalaControle implements Mediador {

    private final Ventilador ventilador;
    private final Valvula valvula;
    private final Alarme alarme;
    private final Painel painel;

    private Nivel temperatura = Nivel.NORMAL;
    private Nivel pressao = Nivel.NORMAL;
    private boolean alarmeSilenciado;

    public SalaControle(Ventilador ventilador, Valvula valvula, Alarme alarme, Painel painel) {
        this.ventilador = Objects.requireNonNull(ventilador, "ventilador não pode ser nulo");
        this.valvula = Objects.requireNonNull(valvula, "válvula não pode ser nula");
        this.alarme = Objects.requireNonNull(alarme, "alarme não pode ser nulo");
        this.painel = Objects.requireNonNull(painel, "painel não pode ser nulo");
        conectar(ventilador);
        conectar(valvula);
        conectar(alarme);
        conectar(painel);
    }

    /** Liga um componente (por exemplo, um sensor) a esta sala. */
    public void conectar(Componente componente) {
        Objects.requireNonNull(componente, "componente não pode ser nulo").definirMediador(this);
    }

    @Override
    public void notificar(Componente origem, Evento evento) {
        Objects.requireNonNull(origem, "origem não pode ser nula");
        Objects.requireNonNull(evento, "evento não pode ser nulo");

        switch (evento) {
            case TEMPERATURA_ALTA -> temperatura = Nivel.ALERTA;
            case TEMPERATURA_CRITICA -> temperatura = Nivel.CRITICO;
            case TEMPERATURA_NORMALIZADA -> temperatura = Nivel.NORMAL;
            case PRESSAO_ALTA -> pressao = Nivel.ALERTA;
            case PRESSAO_CRITICA -> pressao = Nivel.CRITICO;
            case PRESSAO_NORMALIZADA -> pressao = Nivel.NORMAL;
            case SILENCIAR_ALARME -> {
                tratarSilenciamento();
                return;
            }
        }
        painel.exibir(origem.getNome() + ": " + evento.getDescricao());
        aplicarRegras();
    }

    public Nivel getNivelTemperatura() {
        return temperatura;
    }

    public Nivel getNivelPressao() {
        return pressao;
    }

    private void tratarSilenciamento() {
        Nivel pior = pior();
        if (pior == Nivel.CRITICO) {
            painel.exibir("silenciamento recusado: há condição crítica ativa");
            return;
        }
        if (pior == Nivel.NORMAL) {
            painel.exibir("nada a silenciar: o alarme não está ativo");
            return;
        }
        alarmeSilenciado = true;
        painel.exibir("alarme silenciado pelo operador");
        aplicarRegras();
    }

    private void aplicarRegras() {
        Nivel pior = pior();
        if (pior != Nivel.ALERTA) {
            alarmeSilenciado = false;
        }

        boolean alarmeDeve = pior == Nivel.CRITICO || (pior == Nivel.ALERTA && !alarmeSilenciado);
        if (alarmeDeve) {
            alarme.disparar();
        } else {
            alarme.silenciar();
        }

        if (temperatura != Nivel.NORMAL || pressao == Nivel.CRITICO) {
            ventilador.ligar();
        } else {
            ventilador.desligar();
        }

        if (pressao != Nivel.NORMAL) {
            valvula.abrir();
        } else {
            valvula.fechar();
        }
    }

    private Nivel pior() {
        return temperatura.compareTo(pressao) >= 0 ? temperatura : pressao;
    }
}
