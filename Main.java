import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        FilaHospital fila = new FilaHospital();

        // IDs escolhidos de proposito para colidir na tabela (capacidade = 7):
        fila.registrarPaciente(new Paciente(3, "Ana Silva", "NORMAL"));
        Thread.sleep(50);
        fila.registrarPaciente(new Paciente(10, "Carlos Mendes", "URGENTE"));
        Thread.sleep(50);
        fila.registrarPaciente(new Paciente(5, "Beatriz Costa", "NORMAL"));
        Thread.sleep(50);
        fila.registrarPaciente(new Paciente(17, "Diego Santos", "EMERGENCIA"));

        fila.mostrarTabelaHash();
        fila.mostrarOrdemChegada();

        System.out.println("\nBuscando diretamente a ficha 10: " + fila.buscarPaciente(10));

        System.out.println("\n--- Iniciando atendimento ---");
        System.out.println("Atendendo: " + fila.atenderProximoPaciente());
        System.out.println("Atendendo: " + fila.atenderProximoPaciente());

        fila.mostrarTabelaHash();
        fila.mostrarOrdemChegada();
    }
}


class Paciente {
    private int id;          
    private String nome;
    private String prioridade;
    private LocalTime horaChegada;

    public Paciente(int id, String nome, String prioridade) {
        this.id = id;
        this.nome = nome;
        this.prioridade = prioridade;
        this.horaChegada = LocalTime.now();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getPrioridade() { return prioridade; }
    public LocalTime getHoraChegada() { return horaChegada; }

    @Override
    public String toString() {
        return "Ficha " + id + " - " + nome + " [" + prioridade + "] - chegada: " + horaChegada;
    }
}

class FilaHospital {

    // como o encadeamento separado resolve o problema.
    private static final int CAPACIDADE = 7;

    private LinkedList<Paciente>[] tabelaHash;
    private Queue<Integer> ordemChegada;
    private int totalPacientes;

    @SuppressWarnings("unchecked")
    public FilaHospital() {
        tabelaHash = new LinkedList[CAPACIDADE];
        for (int i = 0; i < CAPACIDADE; i++) {
            tabelaHash[i] = new LinkedList<>();
        }
        ordemChegada = new LinkedList<>();
        totalPacientes = 0;
    }

    // Funcao hash simples: resto da divisao pela capacidade
    private int hash(int id) {
        return id % CAPACIDADE;
    }

    public void registrarPaciente(Paciente paciente) {
        int indice = hash(paciente.getId());

        for (Paciente p : tabelaHash[indice]) {
            if (p.getId() == paciente.getId()) {
                System.out.println("Ficha " + paciente.getId() + " ja cadastrada!");
                return;
            }
        }

        tabelaHash[indice].add(paciente);
        ordemChegada.add(paciente.getId());
        totalPacientes++;

        System.out.println("Registrado: ficha " + paciente.getId()
                + " -> indice da tabela hash: " + indice
                + " | posicao na fila de atendimento: " + totalPacientes);
    }


    public Paciente buscarPaciente(int id) {
        int indice = hash(id);
        for (Paciente p : tabelaHash[indice]) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Atende sempre o paciente que chegou primeiro (independente da posicao no array)
    public Paciente atenderProximoPaciente() {
        if (ordemChegada.isEmpty()) {
            System.out.println("Nao ha pacientes na fila.");
            return null;
        }

        int idProximo = ordemChegada.poll();
        Paciente atendido = buscarPaciente(idProximo);

        int indice = hash(idProximo);
        tabelaHash[indice].remove(atendido);
        totalPacientes--;

        return atendido;
    }

    public void mostrarTabelaHash() {
        System.out.println("\n--- Estado interno da tabela hash ---");
        for (int i = 0; i < CAPACIDADE; i++) {
            System.out.print("[" + i + "] -> ");
            if (tabelaHash[i].isEmpty()) {
                System.out.println("vazio");
            } else {
                for (Paciente p : tabelaHash[i]) {
                    System.out.print("ficha" + p.getId() + " ");
                }
                System.out.println();
            }
        }
    }

    public void mostrarOrdemChegada() {
        System.out.println("\n--- Ordem real de chegada (proximo a ser atendido primeiro) ---");
        System.out.println(ordemChegada);
    }
}
