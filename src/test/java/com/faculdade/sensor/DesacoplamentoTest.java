package com.faculdade.sensor;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DesacoplamentoTest {

    @Test
    void nenhumComponenteDeveGuardarReferenciaParaOutroComponente() {
        List<Class<?>> colegas = List.of(Componente.class, SensorMonitorado.class, SensorTemperatura.class,
                SensorPressao.class, Ventilador.class, Valvula.class, Alarme.class, Painel.class);

        for (Class<?> classe : colegas) {
            for (Field campo : classe.getDeclaredFields()) {
                assertFalse(Componente.class.isAssignableFrom(campo.getType()),
                        classe.getSimpleName() + "." + campo.getName() + " referencia outro componente");
            }
        }
    }

    @Test
    void oUnicoCampoDeTipoMediadorDeveEstarNaClasseBase() {
        assertTrue(java.util.Arrays.stream(Componente.class.getDeclaredFields())
                .anyMatch(f -> f.getType() == Mediador.class));
        for (Class<?> classe : List.of(SensorMonitorado.class, Ventilador.class, Valvula.class, Alarme.class,
                Painel.class)) {
            for (Field campo : classe.getDeclaredFields()) {
                assertNotEquals(Mediador.class, campo.getType(), classe.getSimpleName());
            }
        }
    }

    @Test
    void sensorDeveFuncionarComQualquerImplementacaoDeMediador() {
        List<String> chamadas = new ArrayList<>();

        Mediador outro = (origem, evento) -> chamadas.add(origem.getNome() + ":" + evento);
        SensorPressao sensor = new SensorPressao("P-01");
        sensor.definirMediador(outro);

        sensor.ler(11);

        assertEquals(List.of("P-01:PRESSAO_CRITICA"), chamadas);
    }

    @Test
    void trocarOMediadorDeUmSensorDeveMudarParaOndeVaoOsEventos() {
        List<Evento> primeiro = new ArrayList<>();
        List<Evento> segundo = new ArrayList<>();
        SensorTemperatura sensor = new SensorTemperatura("T-01");

        sensor.definirMediador((o, e) -> primeiro.add(e));
        sensor.ler(85);
        sensor.definirMediador((o, e) -> segundo.add(e));
        sensor.ler(95);

        assertEquals(List.of(Evento.TEMPERATURA_ALTA), primeiro);
        assertEquals(List.of(Evento.TEMPERATURA_CRITICA), segundo);
    }

    @Test
    void novoTipoDeSensorDeveEntrarNaSalaSemAlterarOMediador() {

        SensorMonitorado vibracao = new SensorMonitorado("V-01", 4, 7) {
            @Override
            protected Evento eventoPara(Nivel nivel) {
                return nivel == Nivel.NORMAL ? Evento.PRESSAO_NORMALIZADA : Evento.PRESSAO_CRITICA;
            }
        };
        Valvula valvula = new Valvula("VAL-01");
        SalaControle sala = new SalaControle(new Ventilador("VENT"), valvula, new Alarme("ALARME"),
                new Painel("PAINEL"));
        sala.conectar(vibracao);

        vibracao.ler(8);

        assertTrue(valvula.isAberta());
    }
}
