import java.util.*;
import java.io.*;

/**
* This is the Main class.
* @author Zilin Weng
* @version Java 17.0.1
*/
class Main {

  /**
  * A void function that clears the screen
  */
  public static void clearScreen(){
    System.out.print("\033[H\033[2J");  
    System.out.flush(); 
  }

  /**
  * A function that reads the scoreboard and stores each line in an ArrayList
  * @return an ArrayList<String> stored in scoreboard
  */
  public static ArrayList<String> scoreboardRead() throws IOException{
    ArrayList<String> scoreboard = new ArrayList<String>();
    BufferedReader inputStream = null;
    try {
			inputStream = new BufferedReader(new FileReader("scoreboard.txt"));
			String line;
			//reads every line on the scoreboard file and adds it to an ArrayList
      while ((line = inputStream.readLine()) != null) {
        scoreboard.add(line);
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
    return scoreboard;
  }

  /**
  * A void function that sorts the scoreboard and then writes to the scoreboard file
  * @param winner a String that represents the name of the winner
  */
  public static void scoreboardWrite(String winner)throws IOException{
    BufferedWriter bw = null;
    //creates an ArrayList using the ScoreboardRead() function
    ArrayList<String>scores = scoreboardRead();
    ArrayList<String>names = new ArrayList<String>();
    ArrayList<String> wins = new ArrayList<String>();
		bw = new BufferedWriter(new FileWriter("scoreboard.txt", false));
    boolean onBoard = false;
    
      for(int i = 0; i < scores.size(); i++){
        //splits every line by spaces and adds it to an array
        String[] line = scores.get(i).split(" ");
        //the first index in the line is the name
        names.add(line[0]);
        //the second index in the line is the number of wins
        wins.add(line[1]);
      }

      for(int i = 0; i < names.size(); i++){
        if(names.get(i).equals(winner)){
          onBoard = true;
          //increases the number of wins by 1
          wins.set(i, Integer.toString(Integer.parseInt(wins.get(i))+1)); 
        }
      }

      if(!onBoard){
        //writes a new line if the winner is not on the board yet
        names.add(winner);
        wins.add("1");
      }

    //uses selection sort to sort the parallel arrays by number of wins
    int smallestIndex;

		for (int i = 0; i < wins.size(); i++) {
			smallestIndex = i;
			for (int j = i + 1; j < wins.size(); j++) {
				if (Integer.parseInt(wins.get(j))<Integer.parseInt(wins.get(smallestIndex))){
					smallestIndex = j;
				}
			}

			String tempWins = wins.get(smallestIndex);
			wins.set(smallestIndex, wins.get(i));
			wins.set(i, tempWins);

      String tempNames = names.get(smallestIndex);
      names.set(smallestIndex, names.get(i));
      names.set(i, tempNames);
    }

    //writes the values from the ArrayLists into the file in the sorted order
    try{
      for(int i = 0; i < names.size(); i++){
        bw.write(names.get(i) + " " + wins.get(i));
        bw.newLine();
      }
    }
    catch(IOException e){
      System.out.println("Error!");
    }
    finally{
      if(bw!=null){
        bw.close();
      }
    }
  }

  /**
  * A void function that prints out the scoreboard
  * @param scoreboard an ArrayList<String> that represents the scoreboard
  */
  public static void scoreboardPrint(ArrayList<String>scoreboard){
    //prints the scoreboard backwards 
    //ensures greatest number of wins is at the top
    for(int i = scoreboard.size()-1; i >= 0; i--){
      System.out.println(scoreboard.get(i));
    }
  }

  /**
  * A recursive function that determines the best move for the CPU to use
  * @param stones a number that represents the number of stones in the pile
  * @return a number stored in bestMove
  */
  public static int minimax(int stones){
    int bestMove = 0;
    ArrayList<Integer> moves = new ArrayList<Integer>();
    moves.add(minimaxHelper(stones - 1, false));
    moves.add(minimaxHelper(stones - 2, false));
    moves.add(minimaxHelper(stones - 3, false));

    for(int i = 0; i < moves.size(); i++){
      if(moves.get(i) > moves.get(bestMove)){
        bestMove = i;
      }
    }
    return bestMove + 1;
  }

  /**
  * A helper function for the minimax function that contains the recursive calls
  * @param stones a number that represents the number of stones left in the pile
  * @param cpuTurn a boolean that represents whether or not it is the CPU's turn
  * @return max a number that represents the maximizing move
  * @return min a number that represents the minimizing move
  */
  
  public static int minimaxHelper(int stones, boolean cpuTurn){
    //base cases
    //when all the stones are gone and it is the CPU's turn,
    //this means the CPU has taken all the stones out of the pile
    //the CPU has won so we return a good value (1)
    if(stones == 0 && !cpuTurn){
      return 1;
    }
    //when all the stones are gone and it is the not CPU's turn,
    //this means the player has taken all the stones out of the pile
    //the CPU has lost so we return a bad value (-1)
    else if(stones == 0 && cpuTurn){
      return -1;
    }
    else if(stones<0){
      return 0; 
    }

    if(cpuTurn){
      int max = Integer.MIN_VALUE;
      //three possible moves
      int value1 = minimaxHelper(stones - 1, false);
      int value2 = minimaxHelper(stones - 2, false);
      int value3 = minimaxHelper(stones - 3, false);

      //since it is the CPU's turn, we must return the best possible move
      //this will be the maximum value of the three possible moves
      max = Math.max(Math.max(value1, value2), value3);
      return max;
      
    }
    else{
      int min = Integer.MAX_VALUE;
      //three possible moves
      int value1 = minimaxHelper(stones-1, true);
      int value2 = minimaxHelper(stones-2, true);
      int value3 = minimaxHelper(stones-3, true);

      //since it is not the CPU's turn, we assume the player will make the best move for them
      //this will mean that we must return the minimum value for us
      min = Math.min(Math.min(value1, value2), value3);
      return min;
    }
  }

  /** 
  * A function that receives input from the user on how many stones to remove
  * @return stones a number that represents the number of stones being removed
  */
  public static int noOfStones()throws InputMismatchException{
    Scanner sc = new Scanner(System.in);
    int stones = 0;
    while(true){
      try{
        stones = sc.nextInt();
      }
      catch(InputMismatchException e){
        System.out.print("Please enter a number: ");
        stones = noOfStones();
      }
      if(stones > 0 && stones < 4){
        return stones;
      }
      else{
        System.out.print("Please enter a number that is between 1-3: ");
      }
    }
  }

  /**
  * A void function that displays the main menu of the game
  */

  public static void main(String[] args)throws IOException {
    Scanner sc = new Scanner(System.in);
    //displays the meny
    System.out.println("1. 2 Player Game");
    System.out.println("2. 1 Player Game");
    System.out.println("3. How to play");
    System.out.println("4. Scoreboard");
    System.out.print("Please enter an option and press enter: ");
    
    String option = sc.nextLine();

    while(true){
      //prints the rules of the game
      if(option.equals("3")){
        System.out.println("There will be a random number of stones between 15 and 30 in a pile.");
        System.out.println("Two players alternate turns by entering between 1-3 stones to take out of the pile.");
        System.out.println("The player who takes the last stone from the pile wins!");
      }
        //prints the scoreboard file
      else if(option.equals("4")){
        System.out.println();
        System.out.println("Scoreboard");
        scoreboardPrint(scoreboardRead());
      }
      //clears the screen and begins two player game
      else if(option.equals("1")){
        clearScreen();
        gameOfNim();
        break;
      }
      //clears the screen ad begins single player game
      else if(option.equals("2")){
        clearScreen();
        gameOfNimAI();
        break;
      }

      System.out.print("Please enter an option and press enter: ");
      option = sc.nextLine();
    }
  }

  /** 
  * A void function that runs the 2 player Game of Nim
  */
  public static void gameOfNim()throws IOException{
    Scanner sc = new Scanner(System.in);
    Random random = new Random();
    
    //asking for players' names
    System.out.print("Please enter Player 1's name: ");
    String player1 = sc.nextLine();
    System.out.print("Please enter Player 2's name: ");
    String player2 = sc.nextLine();
    
    int turn = 0;

    //generates random number of stones between 15 and 30
    int startStones = random.nextInt(16) + 15;
    System.out.println("Number of Stones: " + startStones);
    while(startStones > 0){
      
      //Player 1's turn
      if(turn%2==0){
        System.out.println(player1 + "'s turn!");
        System.out.print("Enter number of stones to take out: ");
        while(true){
          int removeStones = noOfStones();
          //takes out stones from starting pile
          if(removeStones <= startStones){
            startStones = startStones - removeStones;
            break;
          }
          //ran if there are more stones to be removed than stones in pile
          else{
            System.out.print("Please enter a number less than "+startStones+": ");
          } 
        }
        
      }
      //Player 2's turn
      else if(turn%2==1){
        System.out.println(player2 + "'s turn!");
        System.out.print("Enter number of stones to take out: ");
        while(true){
          int removeStones = noOfStones();
          //removes stones from pile
          if(removeStones <= startStones){
            startStones = startStones - removeStones;
            break;
          }
          //ran if there are more stones to be removed than stones in pile
          else{
            System.out.println("Please enter a number less than "+startStones);
          } 
        }
      }
      System.out.println();
      System.out.println("Stones remaining: " + startStones);
      //increases turn by 1 so the players can alternate turns
      turn++;
    }

    clearScreen();

    //prints the winner's name and adds them to scoreboard
    if(turn%2==0){
      System.out.println(player2 + " won!");
      scoreboardWrite(player2);
      System.out.println();
      System.out.println("Scoreboard");
      scoreboardPrint(scoreboardRead());
    }
    else{
      System.out.println(player1 + " won!");
      scoreboardWrite(player1);
      System.out.println();
      System.out.println("Scoreboard");
      scoreboardPrint(scoreboardRead());
    }
  }

  /*
  * A void function that runs the 1 player Game of Nim
  */
  public static void gameOfNimAI()throws IOException{
    Scanner sc = new Scanner(System.in);
    Random random = new Random();
    
    //asking for player's names
    System.out.print("Please enter your name: ");
    String player = sc.nextLine();
    
    int turn = 0;
    //generates random number of stones between 15 and 30
    int startStones = random.nextInt(16) + 15;
    System.out.println("Number of Stones: " + startStones);
    
    while(startStones > 0){
      //Player's turn
      if(turn%2==0){
        System.out.println(player + "'s turn!");
        System.out.print("Enter number of stones to take out: ");
        while(true){
          int removeStones = noOfStones();
          if(removeStones <= startStones){
            startStones = startStones - removeStones;
            break;
          }
          else{
            System.out.print("Please enter a number less than "+startStones+": ");
          } 
        }
        
      }
      //cpu's turn
      else if(turn%2==1){
        System.out.println("CPU's turn!");
        System.out.print("Stones taken out: ");
        //determines number of stones to take out using minimax() function
        int removeStones = minimax(startStones);
        System.out.println(removeStones);
        startStones = startStones - removeStones;
      }
      System.out.println();
      System.out.println("Stones remaining: " + startStones);
      turn++;
    }

    clearScreen();

    //prints out winner's name and adds them to the scoreboard if the player won
    if(turn%2==1){
      System.out.println(player + " won!");
      scoreboardWrite(player);
      System.out.println();
      System.out.println("Scoreboard");
      scoreboardPrint(scoreboardRead());
    }
    else{
      System.out.println("Sorry, the CPU won!");
    } 
  }
}