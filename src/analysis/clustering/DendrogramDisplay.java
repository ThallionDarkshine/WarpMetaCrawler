package analysis.clustering;/**
 * Created by ThallionDarkshine on 9/13/2018.
 */

import analysis.CardPairingData;
import analysis.CardSynergyAnalysis;
import analysis.cardUsageAnalysis.CardUsageAnalysis;
import data.DeckDesc;
import databases.CardDatabase;
import databases.DeckDatabase;
import gameData.ClusterableTeslDeck;
import gameData.TeslCardDesc;
import gameData.TeslDeckDesc;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import webCrawler.WebCrawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DendrogramDisplay extends Application {

    public static void main(String[] args) {
        CardDatabase.loadData(new File("cards"));

        WebCrawler.ldPullCardsData("https://www.legends-decks.com/cards");

        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-weekly-43");
        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-weekly-42");
        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-weekly-41");
        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-weekly-40");
        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-weekly-39");
        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-na-38");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-na-37");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-eu-10");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-na-36");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/esport-tournament-info/?tournament=tesl-na-35");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-eu-9");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-na-34");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-eu-8");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-na-33");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-eu-7");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-na-32");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-eu-6");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-na-31");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-eu-5");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-na-30");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-eu-3");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-na-29");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-eu-2");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-weekly-28");
//        WebCrawler.wmPullTournamentData("https://warpmeta.com/tournament-info/?tournament=tesl-eu-1");

        CardUsageAnalysis.analyzeCardUsage(CardDatabase.getCards().values(), DeckDatabase.getDecks());

        /*Random rng = new Random();
        List<CardPairingData<TeslCardDesc>> pairings = new ArrayList<>();
        *//*for (int i = 0;i < 10;++i) {
            TeslCardDesc c1 = CardDatabase.getRandom(rng);
            TeslCardDesc c2;

            do {
                c2 = CardDatabase.getRandom(rng);
            } while (c1 == c2);

            pairings.add(new CardPairingData<>(c1, c2));
        }*//*
        pairings.add(new CardPairingData<>(CardDatabase.get("Ash Berserker"), CardDatabase.get("Fifth Legion Trainer")));
        pairings.add(new CardPairingData<>(CardDatabase.get("Sentinel Battlemace"), CardDatabase.get("Shrieking Harpy")));
        pairings.add(new CardPairingData<>(CardDatabase.get("Suran Pawnbroker"), CardDatabase.get("Sharp-Eyed Ashkhan")));
        pairings.add(new CardPairingData<>(CardDatabase.get("Marked Man"), CardDatabase.get("Fifth Legion Trainer")));
        pairings.add(new CardPairingData<>(CardDatabase.get("Hlaalu Sharpshooter"), CardDatabase.get("Nord Firebrand")));
        pairings.add(new CardPairingData<>(CardDatabase.get("Barrow Stalker"), CardDatabase.get("Ald Velothi Assassin")));
        pairings.add(new CardPairingData<>(CardDatabase.get("Tree Minder"), CardDatabase.get("Barrow Stalker")));
        pairings.add(new CardPairingData<>(CardDatabase.get("Cloudrest Illusionist"), CardDatabase.get("Ahnassi")));

        for (TeslDeckDesc deck : DeckDatabase.getDecks()) {
            for (CardPairingData pairing : pairings) {
                pairing.addDeckData(deck);
            }
        }

        for (CardPairingData pairing : pairings) {
            pairing.outputBasicAnalysis();
        }*/

        CardSynergyAnalysis.analyzeCardSynergy(new ArrayList<>(DeckDatabase.getDecks()));

        launch(args);

        CardDatabase.dumpData(new File("cards"));
    }

    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        Scene scene = new Scene(pane, 960.0, 640.0);

        Cluster dendrogram = HierarchicalClustering.buildDendrogram(DeckDatabase.buildDecksMatrix());
        dendrogram.dfs(cluster -> {
            System.out.println(cluster.height);

            return 0;
        });
        System.out.println(dendrogram.size);

        final int[] ind = {0};
        dendrogram.ios(cluster -> {
            cluster.index = ind[0]++;

            return 0;
        });

        float hAdj = 620.0f / (ind[0] - 1);
        float wAdj = 940.0f / dendrogram.height;

        primaryStage.setScene(scene);

        primaryStage.show();

        List<Line> lines = new ArrayList<>();

        new Thread(() -> dendrogram.dfs(cluster -> {
            if (cluster.c1 != null && cluster.c2 != null) {
                float l1_x0 = cluster.c1.height * wAdj;
                float l1_y = cluster.c1.index * hAdj;
                float l1_x1 = cluster.height * wAdj;

                Line l1 = new Line(l1_x0 + 10, l1_y + 10, l1_x1 + 10, l1_y + 10);

                float l2_x0 = cluster.c2.height * wAdj;
                float l2_y = cluster.c2.index * hAdj;
                float l2_x1 = cluster.height * wAdj;

                Line l2 = new Line(l2_x0 + 10, l2_y + 10, l2_x1 + 10, l2_y + 10);

                float l3_x = cluster.height * wAdj;
                float l3_y0 = cluster.c1.index * hAdj;
                float l3_y1 = cluster.c2.index * hAdj;

                Line l3 = new Line(l3_x + 10, l3_y0 + 10, l3_x + 10, l3_y1 + 10);

                cluster.gfx.add(l1);
                cluster.gfx.add(l2);
                cluster.gfx.add(l3);

                Platform.runLater(() -> {
                    lines.add(l1);
                    lines.add(l2);
                    lines.add(l3);

                    pane.getChildren().add(l1);
                    pane.getChildren().add(l2);
                    pane.getChildren().add(l3);
                });
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return 0;
        })).start();

        pane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                float cutX = (float) event.getSceneX();
                float cutHeight = cutX / wAdj;

                List<Cluster> clusters = dendrogram.cut(cutHeight);

//                float percentDiffCutoff = Float.parseFloat(new TextInputDialog().showAndWait().get());
//                List<Cluster> clusters = dendrogram.cutFancy(cutHeight, percentDiffCutoff);

                System.out.println(clusters.size() + " clusters.");
                System.out.println();

                Scanner scanner = new Scanner(System.in);

                for (Cluster c : clusters) {
                    System.out.println("Cluster: ");
                    if (c.parent != null) {
                        System.out.println(c.parent.height);
                        System.out.println(c.parent.pair.getOtherChild(c).height);
                    }
                    System.out.println(c.height);
                    System.out.println(c.size);
                    System.out.println();
                    System.out.println("\t\t-----");
                    System.out.println();

                    c.ios(cluster -> {
                        if (cluster.size == 1) {
                            TeslDeckDesc d = ((ClusterableTeslDeck) cluster.data).deck;

                            if (cluster.parent != null) {
                                System.out.println(cluster.parent.height);
                                System.out.println(cluster.parent.pair.getOtherChild(cluster).height);
                            }

                            d.print();
                        }

                        return 0;
                    });

                    System.out.println("\t\t-----");

                    /*System.out.println();
                    System.out.println("Enter color: ");
                    String colorName = scanner.nextLine();
                    Paint paint = Color.valueOf(colorName.toUpperCase());

                    Platform.runLater(() -> {
                        c.ios(cluster -> {
                            for (Shape s : cluster.gfx) {
                                s.setStroke(paint);
                            }

                            return 0;
                        });
                    });*/
                }
            }
        });
    }
}
