   import java.util.Random;
   import java.util.concurrent.*;
   public class Player extends Thread
   {
      public static int numPlayers = 0;
      public int counter = 0;
      public static int index = 0; //index of lowest scoring player, used in findIndexOfLowestScore() method
      int playerPosition;
      Random r = new Random();
      int score = 0;
      int handPos; // 0 = rock, 1 = paper, 2 = scissors
      static Player[] players;
      private static CyclicBarrier barrier;
      public Player(Player[] plyrs)
      {
         chooseHand();
         numPlayers++; // since all the threads are being created separately, no need to lock this static variable since it will be modified by all the threads at different times?
         playerPosition = numPlayers;
         players = plyrs;
      }
      public Player(Player[] plyrs, String winnerThread)
      {
         //This is the winner thread
         barrier = new CyclicBarrier(numPlayers + 1);
         players = plyrs;
         playerPosition = -1;
      }
      public void chooseHand()
      {
         // 0 = rock, 1 = paper, 2 = scissors
         handPos = r.nextInt(3);
      }
      public int getHandPos()
      {
         return handPos;
      }
      public int getNumPlayers()
      {
         return numPlayers;
      }
      public int getPlayerPosition()
      {
         return playerPosition;
      }
      public void changeScore(int num)
      {
         score = score + num;
      }
      public void resetScore()
      {
         score = 0;
      }
      public int getScore()
      {
         return score;
      }
      public void play(Player p2)
      {
         //draw
         if(handPos == p2.getHandPos())
         {
            //Score does not change
         }
         
         // 3 winning conditions for p1, first condition satisfies 2
         else if((handPos == p2.getHandPos()+1) || (handPos == 0  && p2.getHandPos() == 2))
         {
            changeScore(1);
         }
         
         //p2 has won
         else
         {
            changeScore(-1);
         }
         
      }
      public void playAll()
      {
         //System.out.println("Player is entering loop to play against all players: ");
         for(int i = 0; i < numPlayers; i++) //p.getNumPlayers = array lenght (of players) in beginning
         {
            this.play(players[i]); //the iteration against itself will yield a draw, therefore unaffecting the player's score
         }
         //System.out.println("The players score for player " + playerPosition + " is " + this.getScore());
      }
      
      public void findIndexOfLowestScore()
      {
         for(int i = (numPlayers-1); i > -1; i--)
         {
            if(players[i].getScore() < players[index].getScore())
            {
               index = i;
            }
         }
      }
      
      //reset players' scores and handPositions for next round of game
      public void reset()
      {
         for(int i = 0; i < numPlayers; i++)
         {
            this.chooseHand();
            this.resetScore();
         }
      }
      public void run()
      {
      
         if(this.playerPosition != (-1)) //if this is not the winner thread, aka a player thread
         {
            this.reset();
            playAll();
            try
            {
               barrier.await();
            }
            catch (InterruptedException | BrokenBarrierException e)
            {
               e.printStackTrace();
            }
         }
         else //this thread is the winner thread
         {
            try
            {
               barrier.await();
            }
            catch (InterruptedException | BrokenBarrierException e)
            {
               e.printStackTrace();
            }
            findIndexOfLowestScore();            
            //shift all the players who are after the eliminated player down by one position, to eliminate the lowest scoring player
            for(int i = index; i < (numPlayers -1); i++)
            {
               players[index] = players[index + 1];
            }
            
            numPlayers--;
            index = 0; //reset index for the next game round
            //System.out.println("The lowest player has been eliminated");
            if(numPlayers > 1)
            {
               barrier.reset();
               barrier = new CyclicBarrier(numPlayers + 1);
               for(int i = 0; i < numPlayers; i++)
               {
                  //System.out.println("        CREATED NEW THREAD");
                  players[i] = new Player(players);
                  numPlayers--;
               }
               Player winnerThread = new Player(players, "WinnerThread");
               for(int j = 0; j < numPlayers; j++)
               {
                  players[j].start();
               }
               winnerThread.start();
               
            }
            else
            {
               System.out.println("The winning player is......PLAYER (with thread I.D.)" + (players[index].getId()) + "!!");
            }
         }
      }
      
   }