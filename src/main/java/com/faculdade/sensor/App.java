package com.faculdade.sensor;

/** Cliente de demonstração: monta a sala e conta uma história de incidente passo a passo. */
public class App {

    public static void main(String[] args) {
        Ventilador ventilador = new Ventilador("VENT-01");
        Valvula valvula = new Valvula("VAL-01");
        Alarme alarme = new Alarme("ALARME-01");
        Painel painel = new Painel("PAINEL-01");
        SalaControle sala = new SalaControle(ventilador, valvula, alarme, painel);

        SensorTemperatura temperatura = new SensorTemperatura("T-01");
        SensorPressao pressao = new SensorPressao("P-01");
        sala.conectar(temperatura);
        sala.conectar(pressao);

        passo("1. leituras normais (T=70, P=5)");
        temperatura.ler(70);
        pressao.ler(5);
        estado(ventilador, valvula, alarme);

        passo("2. temperatura sobe para 85 °C (alerta)");
        temperatura.ler(85);
        estado(ventilador, valvula, alarme);

        passo("3. operador silencia o alarme");
        painel.solicitarSilenciamento();
        estado(ventilador, valvula, alarme);

        passo("4. pressão sobe para 11 bar (crítica)");
        pressao.ler(11);
        estado(ventilador, valvula, alarme);

        passo("5. operador tenta silenciar de novo");
        painel.solicitarSilenciamento();
        estado(ventilador, valvula, alarme);

        passo("6. temperatura volta a 70 °C, mas a pressão segue crítica");
        temperatura.ler(70);
        estado(ventilador, valvula, alarme);

        passo("7. pressão volta a 5 bar: tudo normalizado");
        pressao.ler(5);
        estado(ventilador, valvula, alarme);

        System.out.println();
        System.out.println("=== Registro exibido no painel ===");
        painel.getMensagens().forEach(m -> System.out.println("  - " + m));
    }

    private static void passo(String titulo) {
        System.out.println();
        System.out.println(titulo);
    }

    private static void estado(Ventilador v, Valvula va, Alarme a) {
        System.out.printf("   ventilador: %-9s válvula: %-8s alarme: %s%n",
                v.isLigado() ? "LIGADO" : "desligado",
                va.isAberta() ? "ABERTA" : "fechada",
                a.isDisparado() ? "SOANDO" : "mudo");
    }
}
