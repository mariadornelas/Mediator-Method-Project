package com.faculdade.sensor;

public class Ventilador extends Componente {

    private boolean ligado;

    public Ventilador(String nome) {
        super(nome);
    }

    public void ligar() {
        ligado = true;
    }

    public void desligar() {
        ligado = false;
    }

    public boolean isLigado() {
        return ligado;
    }
}
