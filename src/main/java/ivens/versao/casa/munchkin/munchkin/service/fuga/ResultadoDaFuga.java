package ivens.versao.casa.munchkin.munchkin.service.fuga;

public record ResultadoDaFuga(int dado, int bonus, boolean fugiu) {

    public int total() {
        return dado + bonus;
    }
}
