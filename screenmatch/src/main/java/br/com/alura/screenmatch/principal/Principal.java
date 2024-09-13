package br.com.alura.screenmatch.principal;

// import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {
    
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu(){
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

			for(int i=1; i<=dados.totalTemporadas(); i++){
				json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}

			temporadas.forEach(System.out::println);

            for(int i=0; i<dados.totalTemporadas(); i++){
                List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();

                for(int j =0; j < episodiosTemporada.size(); j++){
                    System.out.println(episodiosTemporada.get(j).titulo());
                }
            }

            temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

            List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()) // flatMap() é o recurso para utilizar lista dentreo de outra lista
                .collect(Collectors.toList()); // toList() dá uma lista imutável, Coolectos.toList() é mais flexível.

            System.out.println("\n Top 5 episódios");    
            dadosEpisodios.stream()
            .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
            .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
            .limit(5)
            .forEach(System.out::println);
                       
        List<Episodio> episodios = temporadas.stream()
        .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodio(t.numero(), d))
        ).collect(Collectors.toList());

            // List<String> nomesAleatorios = Arrays.asList("Paula", "Fulano", "Bia", "Flávio", "Eraldo", "Nina");
            // nomesAleatorios.stream()
            // .sorted() // ordenei
            // .limit(5)
            // .filter(n -> n.startsWith("N"))
            // .map(n->n.toUpperCase())
            // .forEach(System.out::println); // imprimi    
    }
}

