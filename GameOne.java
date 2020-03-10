import java.util.Random;
import java.util.concurrent.*;
public class GameOne
{
   public static void main(String[] args) throws InterruptedException
   {
      Player[] players = new Player[150];
      for(int i = 0; i < players.length; i++)
      {
         players[i] = new Player(players);
      }
      Player winnerThread = new Player(players, "WinnerThread");
      for(int j = 0; j < players.length; j++)
      {
         
         players[j].start();
      }
      winnerThread.start();
   }
}