package com.faculdade.sensor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SalaControleTest {

    private Ventilador ventilador;
    private Valvula valvula;
    private Alarme alarme;
    private Painel painel;
    private SalaControle sala;
    private SensorTemperatura temperatura;
    private SensorPressao pressao;

    @BeforeEach
    void montarSala() {
        ventilador = new Ventilador("VENT-01");
        valvula = new Valvula("VAL-01");
        alarme = new Alarme("ALARME-01");
        painel = new Painel("PAINEL-01");
        sala = new SalaControle(ventilador, valvula, alarme, painel);
        temperatura = new SensorTemperatura("T-01");
        pressao = new SensorPressao("P-01");
        sala.conectar(temperatura);
        sala.conectar(pressao);
    }

    @Test
    void leiturasNormaisNaoDevemAcionarNada() {
        temperatura.ler(70);
        pressao.ler(5);

        assertFalse(ventilador.isLigado());
        assertFalse(valvula.isAberta());
        assertFalse(alarme.isDisparado());
        assertTrue(painel.getMensagens().isEmpty());
    }

    @Test
    void temperaturaEmAlertaDeveLigarVentiladorEAlarme() {
        temperatura.ler(85);

        assertTrue(ventilador.isLigado());
        assertTrue(alarme.isDisparado());
        assertFalse(valvula.isAberta());
        assertEquals(List.of("T-01: temperatura em alerta"), painel.getMensagens());
    }

    @Test
    void temperaturaCriticaDeveLigarVentiladorEAlarme() {
        temperatura.ler(95);

        assertTrue(ventilador.isLigado());
        assertTrue(alarme.isDisparado());
        assertFalse(valvula.isAberta());
    }

    @Test
    void pressaoEmAlertaDeveAbrirValvulaESoarAlarmeSemLigarVentilador() {
        pressao.ler(9);

        assertTrue(valvula.isAberta());
        assertTrue(alarme.isDisparado());
        assertFalse(ventilador.isLigado());
    }

    @Test
    void pressaoCriticaDeveAbrirValvulaSoarAlarmeELigarVentilador() {
        pressao.ler(11);

        assertTrue(valvula.isAberta());
        assertTrue(alarme.isDisparado());
        assertTrue(ventilador.isLigado());
    }

    @Test
    void normalizacaoDeTudoDeveDesligarTudo() {
        temperatura.ler(95);
        pressao.ler(11);

        temperatura.ler(70);
        pressao.ler(5);

        assertFalse(ventilador.isLigado());
        assertFalse(valvula.isAberta());
        assertFalse(alarme.isDisparado());
    }

    @Test
    void ventiladorDevePermanecerLigadoEnquantoAPressaoEstiverCritica() {
        temperatura.ler(95);
        pressao.ler(11);

        temperatura.ler(70);

        assertTrue(ventilador.isLigado());
        assertTrue(alarme.isDisparado());
        assertTrue(valvula.isAberta());
    }

    @Test
    void alarmeDevePermanecerEnquantoOutraGrandezaAindaEstiverAnormal() {
        temperatura.ler(85);
        pressao.ler(9);

        pressao.ler(5);

        assertTrue(alarme.isDisparado());
        assertTrue(ventilador.isLigado());
        assertFalse(valvula.isAberta());
    }

    @Test
    void operadorDevePoderSilenciarAlarmeEmAlertaSemDesligarOVentilador() {
        temperatura.ler(85);

        painel.solicitarSilenciamento();

        assertFalse(alarme.isDisparado());
        assertTrue(ventilador.isLigado());
        assertEquals("alarme silenciado pelo operador", painel.getMensagens().get(1));
    }

    @Test
    void operadorNaoDevePoderSilenciarEnquantoHouverCondicaoCritica() {
        pressao.ler(11);

        painel.solicitarSilenciamento();

        assertTrue(alarme.isDisparado());
        assertEquals("silenciamento recusado: há condição crítica ativa",
                painel.getMensagens().get(painel.getMensagens().size() - 1));
    }

    @Test
    void pedidoDeSilenciamentoSemAlarmeAtivoDeveSerInformado() {
        painel.solicitarSilenciamento();

        assertFalse(alarme.isDisparado());
        assertEquals(List.of("nada a silenciar: o alarme não está ativo"), painel.getMensagens());
    }

    @Test
    void chegarACriticoDeveRearmarOAlarmeSilenciadoEMantelo() {
        temperatura.ler(85);
        painel.solicitarSilenciamento();
        assertFalse(alarme.isDisparado());

        temperatura.ler(95);
        assertTrue(alarme.isDisparado());

        temperatura.ler(85);
        assertTrue(alarme.isDisparado());
    }

    @Test
    void silenciamentoDeveSerDesfeitoQuandoTudoNormalizar() {
        temperatura.ler(85);
        painel.solicitarSilenciamento();
        temperatura.ler(70);
        assertFalse(alarme.isDisparado());

        temperatura.ler(85);

        assertTrue(alarme.isDisparado());
    }

    @Test
    void silenciamentoNaoDeveSerDesfeitoPorMudancaDentroDoAlerta() {
        temperatura.ler(85);
        painel.solicitarSilenciamento();

        pressao.ler(9);

        assertFalse(alarme.isDisparado());
    }

    @Test
    void painelDeveRegistrarUmaMensagemPorEventoNaOrdem() {
        temperatura.ler(85);
        temperatura.ler(86);
        pressao.ler(11);

        assertEquals(List.of("T-01: temperatura em alerta", "P-01: pressão crítica"), painel.getMensagens());
    }

    @Test
    void mediadorDeveExporOsNiveisQueConhece() {
        temperatura.ler(85);
        pressao.ler(11);

        assertEquals(Nivel.ALERTA, sala.getNivelTemperatura());
        assertEquals(Nivel.CRITICO, sala.getNivelPressao());
    }

    @Test
    void construtorDeveRejeitarComponentesNulos() {
        assertThrows(NullPointerException.class, () -> new SalaControle(null, valvula, alarme, painel));
        assertThrows(NullPointerException.class, () -> new SalaControle(ventilador, null, alarme, painel));
        assertThrows(NullPointerException.class, () -> new SalaControle(ventilador, valvula, null, painel));
        assertThrows(NullPointerException.class, () -> new SalaControle(ventilador, valvula, alarme, null));
    }

    @Test
    void notificarDeveRejeitarArgumentosNulos() {
        assertThrows(NullPointerException.class, () -> sala.notificar(null, Evento.PRESSAO_ALTA));
        assertThrows(NullPointerException.class, () -> sala.notificar(temperatura, null));
        assertThrows(NullPointerException.class, () -> sala.conectar(null));
    }

    @Test
    void duasSalasDevemSerIndependentes() {
        Ventilador ventilador2 = new Ventilador("VENT-02");
        SalaControle sala2 = new SalaControle(ventilador2, new Valvula("VAL-02"), new Alarme("ALARME-02"),
                new Painel("PAINEL-02"));
        SensorTemperatura temperatura2 = new SensorTemperatura("T-02");
        sala2.conectar(temperatura2);

        temperatura2.ler(95);

        assertTrue(ventilador2.isLigado());
        assertFalse(ventilador.isLigado());
        assertEquals(Nivel.NORMAL, sala.getNivelTemperatura());
    }
}
