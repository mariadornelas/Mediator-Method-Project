package com.faculdade.sensor;

public class Alarme extends Componente {

    private boolean disparado;

    public Alarme(String nome) {
        super(nome);
    }

    public void disparar() {
        disparado = true;
    }

    public void silenciar() {
        disparado = false;
    }

    public boolean isDisparado() {
        return disparado;
    }
}
