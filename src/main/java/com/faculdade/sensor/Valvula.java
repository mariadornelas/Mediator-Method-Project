package com.faculdade.sensor;

public class Valvula extends Componente {

    private boolean aberta;

    public Valvula(String nome) {
        super(nome);
    }

    public void abrir() {
        aberta = true;
    }

    public void fechar() {
        aberta = false;
    }

    public boolean isAberta() {
        return aberta;
    }
}
