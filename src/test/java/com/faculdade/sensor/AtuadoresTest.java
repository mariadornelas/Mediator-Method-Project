package com.faculdade.sensor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AtuadoresTest {

    @Test
    void ventiladorDeveLigarEDesligar() {
        Ventilador v = new Ventilador("VENT-01");
        assertFalse(v.isLigado());

        v.ligar();
        assertTrue(v.isLigado());
        v.ligar();   // idempotente
        assertTrue(v.isLigado());
        v.desligar();
        assertFalse(v.isLigado());
    }

    @Test
    void valvulaDeveAbrirEFechar() {
        Valvula v = new Valvula("VAL-01");
        assertFalse(v.isAberta());

        v.abrir();
        assertTrue(v.isAberta());
        v.fechar();
        assertFalse(v.isAberta());
    }

    @Test
    void alarmeDevePoderDispararESilenciar() {
        Alarme a = new Alarme("ALARME-01");
        assertFalse(a.isDisparado());

        a.disparar();
        assertTrue(a.isDisparado());
        a.silenciar();
        assertFalse(a.isDisparado());
    }

    @Test
    void painelDeveGuardarMensagensNaOrdemERejeitarNulo() {
        Painel p = new Painel("PAINEL-01");

        p.exibir("primeira");
        p.exibir("segunda");

        assertEquals(List.of("primeira", "segunda"), p.getMensagens());
        assertThrows(NullPointerException.class, () -> p.exibir(null));
    }

    @Test
    void mensagensDoPainelDevemSerSomenteLeitura() {
        Painel p = new Painel("PAINEL-01");
        p.exibir("x");

        assertThrows(UnsupportedOperationException.class, () -> p.getMensagens().clear());
    }

    @Test
    void pedidoDeSilenciamentoDeveChegarAoMediadorComoEvento() {
        List<Evento> eventos = new ArrayList<>();
        List<Componente> origens = new ArrayList<>();
        Painel p = new Painel("PAINEL-01");
        p.definirMediador((origem, evento) -> {
            origens.add(origem);
            eventos.add(evento);
        });

        p.solicitarSilenciamento();

        assertEquals(List.of(Evento.SILENCIAR_ALARME), eventos);
        assertSame(p, origens.get(0));
    }

    @Test
    void pedidoDeSilenciamentoSemMediadorDeveFalhar() {
        assertThrows(IllegalStateException.class, () -> new Painel("PAINEL-01").solicitarSilenciamento());
    }
}
