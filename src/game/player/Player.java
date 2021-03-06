package game.player;

import game.*;
import game.player.exceptions.*;
import game.settlements.*;
import game.settlements.exceptions.*;
import game.world.*;
import game.tile.*;

public class Player {
    private int score;
    private int villagerCount;
    private int totoroCount;
    private int tigerCount;

    public SettlementManager settlementManager;

    public Player(World existingWorld) {
        this.score = Settings.STARTING_SCORE_COUNT;
        this.villagerCount = Settings.STARTING_VILLAGER_COUNT;
        this.totoroCount = Settings.STARTING_TOTORO_COUNT;
        this.tigerCount = Settings.STARTING_TIGER_COUNT;
        this.settlementManager = new SettlementManager(existingWorld);
    }

    public int getScore() {
        return this.score;
    }

    public int getVillagerCount() {
        return this.villagerCount;
    }

    public int getTotoroCount() {
        return this.totoroCount;
    }

    public int getTigerCount() {
        return this.tigerCount;
    }

    public void useVillagers(int count) throws NotEnoughPiecesException {
        if (this.villagerCount < count) {
            String errorMessage = String.format("You do not have enough Villagers");
            throw new NotEnoughPiecesException(errorMessage);
        }

        this.villagerCount -= count;
    }

    public void useTotoro() throws NotEnoughPiecesException {
        if (this.totoroCount == 0) {
            String errorMessage = String.format("You do not have enough Totoros.");
            throw new NotEnoughPiecesException(errorMessage);
        }

        this.totoroCount -= 1;
    }

    public void useTiger() throws NotEnoughPiecesException {
        if (this.tigerCount == 0) {
            String errorMessage = String.format("You do not have enough Tigers.");
            throw new NotEnoughPiecesException(errorMessage);
        }

        this.tigerCount -= 1;
    }

    public Settlement foundSettlement(Hex settlementHex) throws
            NotEnoughPiecesException,
            BuildConditionsNotMetException
    {
        try {
            this.useVillagers(1);
            Settlement newSettlement = this.settlementManager.foundSettlement(settlementHex);
            this.score += Settings.FOUND_SETTLEMENT_POINTS;
            return newSettlement;
        }
        catch (BuildConditionsNotMetException e) {
            this.villagerCount += 1;
            throw new SettlementAlreadyExistsOnHexException(e.getMessage());
        }
    }

    public void expandSettlement(Settlement existingSettlement, Terrain terrainToExpandOnto) throws
            SettlementCannotBeBuiltOnVolcanoException,
            NotEnoughPiecesException,
            NoHexesToExpandToException
    {
        int numberOfVillagersRequiredToExpand = this.settlementManager.getNumberOfVillagersRequiredToExpand(existingSettlement, terrainToExpandOnto);
        try {
            this.useVillagers(numberOfVillagersRequiredToExpand);
            this.settlementManager.expandSettlement(existingSettlement, terrainToExpandOnto);
        }
        catch (NoHexesToExpandToException e) {
            this.villagerCount += numberOfVillagersRequiredToExpand;
            throw new NoHexesToExpandToException(e.getMessage());
        }
    }

    public void mergeSettlements(World existingWorld) {
        // this.settlementManager.mergeSettlements();
    }

    public void buildTotoroSanctuary(Hex sanctuaryHex) throws
            NotEnoughPiecesException,
            BuildConditionsNotMetException
    {
        try {
            this.useTotoro();
            this.settlementManager.buildTotoroSanctuary(sanctuaryHex);
            this.score += Settings.BUILD_TOTORO_SANCTUARY_POINTS;
        }
        catch (BuildConditionsNotMetException e) {
            this.totoroCount += 1;
            throw new BuildConditionsNotMetException(e.getMessage());
        }
    }

    public void buildTigerPlayground(Hex playgroundHex) throws
            NotEnoughPiecesException,
            BuildConditionsNotMetException
    {
        try {
            this.useTiger();
            this.settlementManager.buildTigerPlayground(playgroundHex);
            this.score += Settings.BUILD_TIGER_PLAYGROUND_POINTS;
        }
        catch (BuildConditionsNotMetException e) {
            this.tigerCount += 1;
            throw new BuildConditionsNotMetException(e.getMessage());
        }
    }

    public int getSizeOfLargestSettlement() {
        int sizeOfLargestSettlement = settlementManager.sizeOfLargestContainedSettlement();
        return sizeOfLargestSettlement;
    }

    public Settlement getLargestSettlement() {
        Settlement largestSettlement = settlementManager.getLargestContainedSettlement();
        return largestSettlement;
    }

    public Settlement getLargestSettlementNotContainingATotoro() {
        Settlement largestSettlementWithoutTotoro = settlementManager.getLargestSettlementNotContainingATotoro();
        return largestSettlementWithoutTotoro;
    }
}