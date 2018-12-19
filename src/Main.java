import analysis.cardUsageAnalysis.CardUsageAnalysis;
import analysis.clustering.*;
import databases.CardDatabase;
import databases.DeckDatabase;
import javafx.util.Callback;
import webCrawler.WebCrawler;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class Main {
    public static void main(String[] args) {
        CardDatabase.loadData(new File("cards"));

        WebCrawler.ldPullCardsData("https://www.legends-decks.com/cards");

        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-na-34");

        CardUsageAnalysis.analyzeCardUsage(CardDatabase.getCards().values(), DeckDatabase.getDecks());

        List<Map.Entry<List<Float>, Clusterable>> decksMatrix = DeckDatabase.buildDecksMatrix();
//        decksMatrix = PCA.transform(decksMatrix, PCA.buildPcaTransform(PCA.toRealMatrix(decksMatrix)));
//        decksMatrix = PCA.applyPca(decksMatrix);
        Cluster dendrogram = HierarchicalClustering.buildDendrogram(decksMatrix);

        dendrogram.dfs(cluster -> {
            System.out.println(cluster.height);

            return 0;
        });

        CardDatabase.dumpData(new File("cards"));
    }
}
