package com.faculdade.sensor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Os sensores isolados, com um mediador espião (lambda): só informam fatos, e só quando o nível muda. */
class SensorMonitoradoTest {

    private final List<Evento> eventos = new ArrayList<>();

    private SensorTemperatura temperatura() {
        SensorTemperatura s = new SensorTemperatura("T-01");
        s.definirMediador((origem, evento) -> eventos.add(evento));
        return s;
    }

    private SensorPressao pressao() {
        SensorPressao s = new SensorPressao("P-01");
        s.definirMediador((origem, evento) -> eventos.add(evento));
        return s;
    }

    @Test
    void leiturasNormaisNaoDevemGerarEvento() {
        SensorTemperatura s = temperatura();

        s.ler(20);
        s.ler(79.9);

        assertTrue(eventos.isEmpty());
        assertEquals(Nivel.NORMAL, s.getNivel());
    }

    @Test
    void deveNotificarSomenteQuandoONivelMuda() {
        SensorTemperatura s = temperatura();

        s.ler(85);
        s.ler(86);   // continua em alerta
        s.ler(89);   // continua em alerta

        assertEquals(List.of(Evento.TEMPERATURA_ALTA), eventos);
    }

    @Test
    void deveEmitirAsTransicoesNaOrdemEmQueOcorrem() {
        SensorTemperatura s = temperatura();

        s.ler(85);
        s.ler(95);
        s.ler(70);

        assertEquals(List.of(Evento.TEMPERATURA_ALTA, Evento.TEMPERATURA_CRITICA, Evento.TEMPERATURA_NORMALIZADA),
                eventos);
    }

    @Test
    void saltoDeNormalParaCriticoDeveEmitirDiretamenteOCritico() {
        SensorTemperatura s = temperatura();

        s.ler(120);

        assertEquals(List.of(Evento.TEMPERATURA_CRITICA), eventos);
    }

    @Test
    void limitesDevemSerInclusivos() {
        SensorTemperatura s = temperatura();

        s.ler(80.0);
        assertEquals(Nivel.ALERTA, s.getNivel());
        s.ler(90.0);
        assertEquals(Nivel.CRITICO, s.getNivel());
    }

    @Test
    void sensorDePressaoDeveUsarSeusProprioseventosELimites() {
        SensorPressao s = pressao();

        s.ler(8.0);
        s.ler(10.0);
        s.ler(5.0);

        assertEquals(List.of(Evento.PRESSAO_ALTA, Evento.PRESSAO_CRITICA, Evento.PRESSAO_NORMALIZADA), eventos);
    }

    @Test
    void origemInformadaAoMediadorDeveSerOProprioSensor() {
        List<Componente> origens = new ArrayList<>();
        SensorTemperatura s = new SensorTemperatura("T-01");
        s.definirMediador((origem, evento) -> origens.add(origem));

        s.ler(95);

        assertSame(s, origens.get(0));
    }

    @Test
    void semMediadorANotificacaoDeveFalharSemAlterarONivel() {
        SensorTemperatura s = new SensorTemperatura("T-01");

        assertThrows(IllegalStateException.class, () -> s.ler(95));

        assertEquals(Nivel.NORMAL, s.getNivel());   // pode tentar de novo depois de conectar
    }

    @Test
    void semMediadorLeituraNormalNaoPrecisaDeMediador() {
        SensorTemperatura s = new SensorTemperatura("T-01");

        assertDoesNotThrow(() -> s.ler(20));
    }

    @Test
    void deveRejeitarLeiturasInvalidas() {
        SensorTemperatura s = temperatura();

        assertThrows(IllegalArgumentException.class, () -> s.ler(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> s.ler(Double.POSITIVE_INFINITY));
        assertTrue(eventos.isEmpty());
    }

    @Test
    void componenteDeveRejeitarNomeInvalidoEMediadorNulo() {
        assertThrows(NullPointerException.class, () -> new SensorTemperatura(null));
        assertThrows(IllegalArgumentException.class, () -> new SensorTemperatura("   "));
        assertThrows(NullPointerException.class, () -> new SensorTemperatura("T").definirMediador(null));
    }

    @Test
    void limitesInvertidosNaoDevemSerAceitos() {
        class SensorInvalido extends SensorMonitorado {
            SensorInvalido() {
                super("X", 90, 80);
            }

            @Override
            protected Evento eventoPara(Nivel nivel) {
                return Evento.TEMPERATURA_ALTA;
            }
        }

        assertThrows(IllegalArgumentException.class, SensorInvalido::new);
    }
}
