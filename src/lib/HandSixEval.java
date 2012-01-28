/*!
HandEval
SpecialKEval
<p>
Copyright 2010 Kenneth J. Shackleton
codingfeedback@gmail.com
http://specialk-coding.blogspot.com/
<p>
 ***********************************************************************
An evolution of this evaluator has been released under Apple's EULA and
is behind the app "Poker Ace" available through iTunes Store. For more
details visit http://itunes.apple.com/us/app/poker-ace/id392530020?mt=8
 ***********************************************************************
<p>
This program gives you software freedom; you can copy, convey,
propagate, redistribute and/or modify this program under the terms of
the GNU General Public License (GPL) as published by the Free Software
Foundation (FSF), either version 3 of the License, or (at your option)
any later version of the GPL published by the FSF.
<p>
This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.
<p>
You should have received a copy of the GNU General Public License along
with this program in a file in the toplevel directory called "GPLv3".
If not, see http://www.gnu.org/licenses/.
<p>
@author Kenneth J. Shackleton
 */

package lib;

import java.util.Random;

import static lib.Constants.*;

public class HandSixEval {
	// Ranks for 6-card evaluation separated
	// into non-flushes and flushes, each with
	// their own respective keys
	private static int rankArray[];
	private static int flushRankArray[];

	// Card face values beginning with ACE_ from
	// index 0 and TWO_ from index 48.
	private static long deckcardsKey[];
	private static int deckcardsFlush[];
	private static int deckcardsSuit[];

	// Array comprising of the flush suits.
	private static short flushCheckArray[];

	static  {
		initialiseDeck();
		initialiseRanking();
		generateFlushCheck();
	}

	private static void initialiseDeck() {
		deckcardsKey = new long[DECK_SIZE];
		deckcardsFlush = new int[DECK_SIZE];
		deckcardsSuit = new int[DECK_SIZE];

		// Enter face values into arrays to later build up the
		// respective keys. The values of ACE and ACE_FLUSH etc.
		// are different.
		int[] face = { ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX,
				FIVE, FOUR, THREE, TWO };
		int[] faceflush = { ACE_FLUSH, KING_FLUSH, QUEEN_FLUSH, JACK_FLUSH,
				TEN_FLUSH, NINE_FLUSH, EIGHT_FLUSH, SEVEN_FLUSH, SIX_FLUSH,
				FIVE_FLUSH, FOUR_FLUSH, THREE_FLUSH, TWO_FLUSH };

		for (int n = 0; n < 13; n++) {
			deckcardsKey[4 * n] = (face[n] << NON_FLUSH_BIT_SHIFT) + SPADE;
			deckcardsKey[4 * n + 1] = (face[n] << NON_FLUSH_BIT_SHIFT) + HEART;
			deckcardsKey[4 * n + 2] = (face[n] << NON_FLUSH_BIT_SHIFT)
					+ DIAMOND;
			deckcardsKey[4 * n + 3] = (face[n] << NON_FLUSH_BIT_SHIFT) + CLUB;

			deckcardsFlush[4 * n] = faceflush[n];
			deckcardsFlush[4 * n + 1] = faceflush[n];
			deckcardsFlush[4 * n + 2] = faceflush[n];
			deckcardsFlush[4 * n + 3] = faceflush[n];

			deckcardsSuit[4 * n] = SPADE;
			deckcardsSuit[4 * n + 1] = HEART;
			deckcardsSuit[4 * n + 2] = DIAMOND;
			deckcardsSuit[4 * n + 3] = CLUB;
		}
	}

	public static int getRankOfSeven(int card_1, int card_2, int card_3, int card_4,
			int card_5, int card_6, int card_7) {
		long KEY = deckcardsKey[card_1] + deckcardsKey[card_2]
				+ deckcardsKey[card_3] + deckcardsKey[card_4]
				+ deckcardsKey[card_5] + deckcardsKey[card_6]
				+ deckcardsKey[card_7];
		int FLUSH_CHECK_KEY = (int) (KEY & SUIT_BIT_MASK);
		int FLUSH_SUIT = flushCheckArray[FLUSH_CHECK_KEY];

		if (FLUSH_SUIT < 0) {
			KEY = (KEY >> NON_FLUSH_BIT_SHIFT);
			return rankArray[KEY < CIRCUMFERENCE_SEVEN ? (int) KEY : (int) KEY
					- CIRCUMFERENCE_SEVEN];
		} else {
			int FLUSH_KEY = (deckcardsSuit[card_1] == FLUSH_SUIT ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == FLUSH_SUIT ? deckcardsFlush[card_2]
							: 0)
					+ (deckcardsSuit[card_3] == FLUSH_SUIT ? deckcardsFlush[card_3]
							: 0)
					+ (deckcardsSuit[card_4] == FLUSH_SUIT ? deckcardsFlush[card_4]
							: 0)
					+ (deckcardsSuit[card_5] == FLUSH_SUIT ? deckcardsFlush[card_5]
							: 0)
					+ (deckcardsSuit[card_6] == FLUSH_SUIT ? deckcardsFlush[card_6]
							: 0)
					+ (deckcardsSuit[card_7] == FLUSH_SUIT ? deckcardsFlush[card_7]
							: 0);
			return flushRankArray[FLUSH_KEY];
		}
	}

	public static void initialiseRanking() {
		rankArray = new int[MAX_NONFLUSH_KEY_INT + 1];
		flushRankArray = new int[MAX_KEY_INT + 1];
		int[] face = { ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX,
				FIVE, FOUR, THREE, TWO };
		int[] faceFlush = { ACE_FLUSH, KING_FLUSH, QUEEN_FLUSH, JACK_FLUSH,
				TEN_FLUSH, NINE_FLUSH, EIGHT_FLUSH, SEVEN_FLUSH, SIX_FLUSH,
				FIVE_FLUSH, FOUR_FLUSH, THREE_FLUSH, TWO_FLUSH };
		int i, j, k, l, m, n, p;

		// Clean all ranks and flush ranks
		for (i = 0; i < MAX_NONFLUSH_KEY_INT + 1; i++) {
			rankArray[i] = 0;
		}
		for (i = 0; i < MAX_FLUSH_KEY_INT + 1; i++) {
			flushRankArray[i] = 0;
		}

		// Non-flush ranks
		for (i = 1; i < 13; i++) {
			for (j = 1; j <= i; j++) {
				for (k = 0; k <= j; k++) {
					for (l = 0; l <= k; l++) {
						for (m = 0; m <= l; m++) {
							for (n = 0; n <= m; n++) {
								if (i != m && j != n) {
									int key = face[i] + face[j] + face[k]
											+ face[l] + face[m] + face[n];
									// The 4*i+0 and 4*m+1 trick prevents
									// flushes
									int rank = FiveEval
											.getBestRankOf(4 * i, 4 * j,
													4 * k, 4 * l,
													4 * m + 1, 4 * n + 1);
									rankArray[key < CIRCUMFERENCE_SEVEN ? key
											: key - CIRCUMFERENCE_SEVEN] = rank;
								}
							}
						}
					}
				}
			}
		}

		// Flush ranks
		// All 6 same suit:
		for (i = 5; i < 13; i++) {
			for (j = 4; j < i; j++) {
				for (k = 3; k < j; k++) {
					for (l = 2; l < k; l++) {
						for (m = 1; m < l; m++) {
							for (n = 0; n < m; n++) {
								int key = faceFlush[i] + faceFlush[j]
										+ faceFlush[k] + faceFlush[l]
										+ faceFlush[m] + faceFlush[n];
								int rank = FiveEval.getBestRankOf(4 * i,
										4 * j, 4 * k, 4 * l, 4 * m, 4 * n);
								flushRankArray[key] = rank;
							}
						}
					}
				}
			}
		}

		// Only 5 same suit:
		for (i = 4; i < 13; i++) {
			for (j = 3; j < i; j++) {
				for (k = 2; k < j; k++) {
					for (l = 1; l < k; l++) {
						for (m = 0; m < l; m++) {
							int key = faceFlush[i] + faceFlush[j]
									+ faceFlush[k] + faceFlush[l]
									+ faceFlush[m];
							// The Two of clubs is the card at index 51, the
							// other cards are all spades
							int rank = FiveEval.getBestRankOf(4 * i, 4 * j,
									4 * k, 4 * l, 4 * m);
							flushRankArray[key] = rank;
						}
					}
				}
			}
		}
	}

	private static void generateFlushCheck() {
		flushCheckArray = new short[MAX_FLUSH_CHECK_SUM + 1];
		int card_1, card_2, card_3, card_4, card_5, card_6;

		// Begin with spades and run no further than clubs
		int SUIT_KEY = SPADE;
		int[] suits = { SPADE, HEART, DIAMOND, CLUB };

		// Initialise all entries of flushCheckArray[] to UNVERIFIED, as yet
		// unchecked.
		// memset(&flushCheckArray[0], UNVERIFIED,
		// sizeof(int)*(MAX_FLUSH_CHECK_SUM+1));
		for (int i = 0; i < MAX_FLUSH_CHECK_SUM + 1; i++) {
			flushCheckArray[i] = UNVERIFIED;
		}

		// 7-card
		for (card_1 = 0; card_1 < NUMBER_OF_SUITS; card_1++) {
			for (card_2 = 0; card_2 <= card_1; card_2++) {
				for (card_3 = 0; card_3 <= card_2; card_3++) {
					for (card_4 = 0; card_4 <= card_3; card_4++) {
						for (card_5 = 0; card_5 <= card_4; card_5++) {
							for (card_6 = 0; card_6 <= card_5; card_6++) {
								int SUIT_COUNT = 0, FLUSH_SUIT_INDEX = -1, CARDS_MATCHED_SO_FAR = 0;
								SUIT_KEY = suits[card_1] + suits[card_2]
										+ suits[card_3] + suits[card_4]
										+ suits[card_5] + suits[card_6];
								if (flushCheckArray[SUIT_KEY] == UNVERIFIED) {
									do {
										FLUSH_SUIT_INDEX++;
										SUIT_COUNT = (suits[card_1] == suits[FLUSH_SUIT_INDEX] ? 1
												: 0)
												+ (suits[card_2] == suits[FLUSH_SUIT_INDEX] ? 1
														: 0)
												+ (suits[card_3] == suits[FLUSH_SUIT_INDEX] ? 1
														: 0)
												+ (suits[card_4] == suits[FLUSH_SUIT_INDEX] ? 1
														: 0)
												+ (suits[card_5] == suits[FLUSH_SUIT_INDEX] ? 1
														: 0)
												+ (suits[card_6] == suits[FLUSH_SUIT_INDEX] ? 1
														: 0);
										CARDS_MATCHED_SO_FAR += SUIT_COUNT;
									} while (CARDS_MATCHED_SO_FAR < 2
											&& FLUSH_SUIT_INDEX < 4);

									// 7-card flush check means flush
									if (SUIT_COUNT > 4) {
										flushCheckArray[SUIT_KEY] = (short) suits[FLUSH_SUIT_INDEX];
									} else {
										flushCheckArray[SUIT_KEY] = (short) NOT_A_FLUSH;
									}
								}
							}
						}
					}
				}
			}
		}
	}


	public static double[] computeSixCardFlopEquityForSpecificCards(int[] holeCards,
			int[] tableCards, int number_of_players) {
		if (holeCards == null || number_of_players <= 0
				|| holeCards.length != 2 * number_of_players) {
			return null;
		}

		// Count and list the undealt cards, i.e. the remaining deck.
		int[] undealtCards = new int[DECK_SIZE];
		int number_of_undealt_cards = 0;

		for (int i = 0; i < DECK_SIZE; i++) {
			boolean bool_add_card = true;
			for (int j = 0; j < 2 * number_of_players; j++) {
				if (holeCards[j] == i) {
					bool_add_card = false;
				}
			}
			for (int j = 0; j < 3; j++) {
				if (tableCards[j] == i) {
					bool_add_card = false;
				}
			}
			if (bool_add_card == true) {
				undealtCards[number_of_undealt_cards] = i;
				number_of_undealt_cards++;
			}
		}
		// //////////

		// Cumulative equities
		long[] equity = new long[MAX_NUMBER_OF_PLAYERS];
		for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
			equity[i] = 0;
		}
		// //////////

		// Record player_rank
		int[] player_rank = new int[MAX_NUMBER_OF_PLAYERS];
		for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
			player_rank[i] = 0;
		}
		// //////////

		// Possible shares in equity
		int[] equity_share = new int[MAX_NUMBER_OF_PLAYERS + 1];
		// The 0th entry is redundant
		equity_share[0] = -1;
		for (int i = 1; i < MAX_NUMBER_OF_PLAYERS + 1; i++) {
			equity_share[i] = CAKE / i;
		}
		// /////////

		// Player table key and suit stems
		long[] player_key = new long[MAX_NUMBER_OF_PLAYERS];
		int[][] player_flush_stem = new int[MAX_NUMBER_OF_PLAYERS][CLUB + 1];
		int[] player_suit_stem = new int[MAX_NUMBER_OF_PLAYERS];

		for (int p = 0; p < number_of_players; p++) {
			int P = 2 * p;

			int card_1 = holeCards[P];
			int card_2 = holeCards[P + 1];

			player_key[p] = deckcardsKey[card_1] + deckcardsKey[card_2];

			player_flush_stem[p][SPADE] = (deckcardsSuit[card_1] == SPADE ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == SPADE ? deckcardsFlush[card_2]
							: 0);

			player_flush_stem[p][HEART] = (deckcardsSuit[card_1] == HEART ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == HEART ? deckcardsFlush[card_2]
							: 0);

			player_flush_stem[p][DIAMOND] = (deckcardsSuit[card_1] == DIAMOND ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == DIAMOND ? deckcardsFlush[card_2]
							: 0);

			player_flush_stem[p][CLUB] = (deckcardsSuit[card_1] == CLUB ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == CLUB ? deckcardsFlush[card_2]
							: 0);

			player_suit_stem[p] = deckcardsSuit[holeCards[P]]
					+ deckcardsSuit[holeCards[P + 1]];
		}
		// //////

		int table_card_1 = tableCards[0];
		int table_card_2 = tableCards[1];
		int table_card_3 = tableCards[2];

		long flop_table_key = deckcardsKey[table_card_1]
				+ deckcardsKey[table_card_2] + deckcardsKey[table_card_3];

		// int player_defect = number_of_players-1;
		int number_of_players_sharing_the_pot = 1;
		int current_best_rank;
		int next_rank;

		// Begin calculating equities by dealing out all possible
		// combinations of five table cards, adding up the incremental
		// equities to find the total integral equities.

//		Random rnd = new Random();
//		int counter = 0;
		
		int i = 0;
		do {
			int table_card_4 = undealtCards[i];
			long table_key = flop_table_key
					+ deckcardsKey[table_card_4];
			
			// Reset table_flush_key.
			int table_flush_key = UNVERIFIED;
			// Reset number of interested players.
			number_of_players_sharing_the_pot = 1;
			// //////
			long KEY = table_key + player_key[0];

			int FLUSH_CHECK_KEY = (int) (KEY & SUIT_BIT_MASK);
			int FLUSH_SUIT = flushCheckArray[FLUSH_CHECK_KEY];

			if (FLUSH_SUIT < 0) {
				KEY = (KEY >> NON_FLUSH_BIT_SHIFT);
				current_best_rank = rankArray[KEY < CIRCUMFERENCE_SEVEN ? (int) KEY
						: (int) KEY - CIRCUMFERENCE_SEVEN];
				player_rank[0] = current_best_rank;
			} else {
				table_flush_key = (table_flush_key == UNVERIFIED ? (deckcardsSuit[table_card_1] == FLUSH_SUIT ? deckcardsFlush[table_card_1]
						: 0)
						+ (deckcardsSuit[table_card_2] == FLUSH_SUIT ? deckcardsFlush[table_card_2]
								: 0)
						+ (deckcardsSuit[table_card_3] == FLUSH_SUIT ? deckcardsFlush[table_card_3]
								: 0)
						+ (deckcardsSuit[table_card_4] == FLUSH_SUIT ? deckcardsFlush[table_card_4]
								: 0)
						: table_flush_key);
				int player_flush_key = table_flush_key
						+ player_flush_stem[0][FLUSH_SUIT];
				current_best_rank = flushRankArray[player_flush_key];
				player_rank[0] = current_best_rank;
			}

			int n = 1;
			do {
				KEY = table_key + player_key[n];

				FLUSH_CHECK_KEY = (int) (KEY & SUIT_BIT_MASK);
				FLUSH_SUIT = flushCheckArray[FLUSH_CHECK_KEY];

				if (FLUSH_SUIT == NOT_A_FLUSH) {
					KEY = (KEY >> NON_FLUSH_BIT_SHIFT);
					next_rank = rankArray[((int) KEY < CIRCUMFERENCE_SEVEN ? (int) KEY
							: (int) KEY - CIRCUMFERENCE_SEVEN)];
					player_rank[n] = next_rank;
				} else {
					// ** This could be a good place to optimize **
					table_flush_key = (table_flush_key == UNVERIFIED ? (deckcardsSuit[table_card_1] == FLUSH_SUIT ? deckcardsFlush[table_card_1]
							: 0)
							+ (deckcardsSuit[table_card_2] == FLUSH_SUIT ? deckcardsFlush[table_card_2]
									: 0)
							+ (deckcardsSuit[table_card_3] == FLUSH_SUIT ? deckcardsFlush[table_card_3]
									: 0)
							+ (deckcardsSuit[table_card_4] == FLUSH_SUIT ? deckcardsFlush[table_card_4]
									: 0)
							: table_flush_key);
					int player_flush_key = table_flush_key
							+ player_flush_stem[n][FLUSH_SUIT];
					next_rank = flushRankArray[player_flush_key];
					player_rank[n] = next_rank;
				}

				// // Compare the player_rank
				//
				// Case: new outright strongest player
				if (current_best_rank < next_rank) {
					// Update current best rank
					current_best_rank = next_rank;
					// Reset number of players interested
					number_of_players_sharing_the_pot = 1;
				} else if (current_best_rank == next_rank) { // Case: pot
																// shared
																// with
																// another
																// player
				// Increment by 1 the number of interest players
					number_of_players_sharing_the_pot++;
				}
				// Last Case: new player loses
				// There is nothing we need to do.
				// /////
				n++;
			} while (n < number_of_players);

			// Calculate incremental equity
			int incremental_equity = equity_share[number_of_players_sharing_the_pot];

			// Add to the cumulative equities
			int p = 0;
			do {
				if (player_rank[p] == current_best_rank) {
					equity[p] += incremental_equity;
				}
				p++;
			} while (p < number_of_players);
			i++;
		} while (i < number_of_undealt_cards);

		long total_equity = 0;
		for (i = 0; i < number_of_players; i++) {
			total_equity += equity[i];
		}

		double[] equity_percentage = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0 };

		// int Equity[] = new int[] {EQUITY_TWO, EQUITY_THREE, EQUITY_FOUR,
		// EQUITY_FIVE, EQUITY_SIX, EQUITY_SEVEN, EQUITY_EIGHT};
		// double equity_sum = 0.0;

		for (i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
			equity_percentage[i] = 100 * (equity[i] + 0.0) / total_equity;
			// equity_sum += equity_percentage[i];
		}
		// equity_percentage[player_defect] = 100.0f - equity_sum;
//		String s = String.format("%.8f_%.8f_%.8f_%.8f_%.8f_%.8f_%.8f_%.8f",
//				equity_percentage[0], equity_percentage[1],
//				equity_percentage[2], equity_percentage[3],
//				equity_percentage[4], equity_percentage[5],
//				equity_percentage[6], equity_percentage[7]);

		return equity_percentage;
	}
	public static double[] computeEstimatedFlopEquityForSpecificCards(int[] holeCards,
			int[] tableCards, int number_of_players) {
		if (holeCards == null || number_of_players <= 0
				|| holeCards.length != 2 * number_of_players) {
			return null;
		}

		// Count and list the undealt cards, i.e. the remaining deck.
		int[] undealtCards = new int[DECK_SIZE];
		int number_of_undealt_cards = 0;

		for (int i = 0; i < DECK_SIZE; i++) {
			boolean bool_add_card = true;
			for (int j = 0; j < 2 * number_of_players; j++) {
				if (holeCards[j] == i) {
					bool_add_card = false;
				}
			}
			for (int j = 0; j < 3; j++) {
				if (tableCards[j] == i) {
					bool_add_card = false;
				}
			}
			if (bool_add_card == true) {
				undealtCards[number_of_undealt_cards] = i;
				number_of_undealt_cards++;
			}
		}
		// //////////

		// Cumulative equities
		long[] equity = new long[MAX_NUMBER_OF_PLAYERS];
		for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
			equity[i] = 0;
		}
		// //////////

		// Record player_rank
		int[] player_rank = new int[MAX_NUMBER_OF_PLAYERS];
		for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
			player_rank[i] = 0;
		}
		// //////////

		// Possible shares in equity
		int[] equity_share = new int[MAX_NUMBER_OF_PLAYERS + 1];
		// The 0th entry is redundant
		equity_share[0] = -1;
		for (int i = 1; i < MAX_NUMBER_OF_PLAYERS + 1; i++) {
			equity_share[i] = CAKE / i;
		}
		// /////////
		
		// Possible type
		int [] equity_type = new int[10];
		int num_type = 0, total_count = 0;
		double [][] share_amount = new double[10][MAX_NUMBER_OF_PLAYERS];
		int [] initial_rank = new int [MAX_NUMBER_OF_PLAYERS];
		int [] equity_count = new int [MAX_NUMBER_OF_PLAYERS];
		int bestrank, noPlayers, hash;
		
		bestrank = -1;
		noPlayers = 0;
		hash = 0;
		for( int p=0; p<number_of_players; p++ ){
			initial_rank[p] = FiveEval.getBestRankOf(holeCards[2*p], holeCards[2*p+1], 
					tableCards[0], tableCards[1], tableCards[2]);
			if( bestrank<initial_rank[p] ){
				bestrank = initial_rank[p];
				noPlayers = 1;
			}else if( bestrank==initial_rank[p] ){
				noPlayers++;
			}
		}
		for( int p=0; p<number_of_players; p++ ){
			if(bestrank==initial_rank[p]){
				hash = hash*2+1;
				share_amount[0][p]=equity_share[noPlayers];
			}else{
				hash = hash*2;
				share_amount[0][p]=0;
			}
		}
		equity_count[0]=0;
		equity_type[0]=hash;
		num_type = 1;
		
		// /////////

		// Player table key and suit stems
		long[] player_key = new long[MAX_NUMBER_OF_PLAYERS];
		int[][] player_flush_stem = new int[MAX_NUMBER_OF_PLAYERS][CLUB + 1];
		int[] player_suit_stem = new int[MAX_NUMBER_OF_PLAYERS];

		for (int p = 0; p < number_of_players; p++) {
			int P = 2 * p;

			int card_1 = holeCards[P];
			int card_2 = holeCards[P + 1];

			player_key[p] = deckcardsKey[card_1] + deckcardsKey[card_2];

			player_flush_stem[p][SPADE] = (deckcardsSuit[card_1] == SPADE ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == SPADE ? deckcardsFlush[card_2]
							: 0);

			player_flush_stem[p][HEART] = (deckcardsSuit[card_1] == HEART ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == HEART ? deckcardsFlush[card_2]
							: 0);

			player_flush_stem[p][DIAMOND] = (deckcardsSuit[card_1] == DIAMOND ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == DIAMOND ? deckcardsFlush[card_2]
							: 0);

			player_flush_stem[p][CLUB] = (deckcardsSuit[card_1] == CLUB ? deckcardsFlush[card_1]
					: 0)
					+ (deckcardsSuit[card_2] == CLUB ? deckcardsFlush[card_2]
							: 0);

			player_suit_stem[p] = deckcardsSuit[holeCards[P]]
					+ deckcardsSuit[holeCards[P + 1]];
		}
		// //////

		int table_card_1 = tableCards[0];
		int table_card_2 = tableCards[1];
		int table_card_3 = tableCards[2];

		long flop_table_key = deckcardsKey[table_card_1]
				+ deckcardsKey[table_card_2] + deckcardsKey[table_card_3];

		// int player_defect = number_of_players-1;
		int number_of_players_sharing_the_pot = 1;
		int current_best_rank;
		int next_rank;

		// Begin calculating equities by dealing out all possible
		// combinations of five table cards, adding up the incremental
		// equities to find the total integral equities.

//		Random rnd = new Random();
//		int counter = 0;
		
		int i = 0;
		do {
			int table_card_4 = undealtCards[i];
			long table_key = flop_table_key
					+ deckcardsKey[table_card_4];
			
			// Reset table_flush_key.
			int table_flush_key = UNVERIFIED;
			// Reset number of interested players.
			number_of_players_sharing_the_pot = 1;
			// //////
			long KEY = table_key + player_key[0];

			int FLUSH_CHECK_KEY = (int) (KEY & SUIT_BIT_MASK);
			int FLUSH_SUIT = flushCheckArray[FLUSH_CHECK_KEY];

			if (FLUSH_SUIT < 0) {
				KEY = (KEY >> NON_FLUSH_BIT_SHIFT);
				current_best_rank = rankArray[KEY < CIRCUMFERENCE_SEVEN ? (int) KEY
						: (int) KEY - CIRCUMFERENCE_SEVEN];
				player_rank[0] = current_best_rank;
			} else {
				table_flush_key = (table_flush_key == UNVERIFIED ? (deckcardsSuit[table_card_1] == FLUSH_SUIT ? deckcardsFlush[table_card_1]
						: 0)
						+ (deckcardsSuit[table_card_2] == FLUSH_SUIT ? deckcardsFlush[table_card_2]
								: 0)
						+ (deckcardsSuit[table_card_3] == FLUSH_SUIT ? deckcardsFlush[table_card_3]
								: 0)
						+ (deckcardsSuit[table_card_4] == FLUSH_SUIT ? deckcardsFlush[table_card_4]
								: 0)
						: table_flush_key);
				int player_flush_key = table_flush_key
						+ player_flush_stem[0][FLUSH_SUIT];
				current_best_rank = flushRankArray[player_flush_key];
				player_rank[0] = current_best_rank;
			}

			int n = 1;
			do {
				KEY = table_key + player_key[n];

				FLUSH_CHECK_KEY = (int) (KEY & SUIT_BIT_MASK);
				FLUSH_SUIT = flushCheckArray[FLUSH_CHECK_KEY];

				if (FLUSH_SUIT == NOT_A_FLUSH) {
					KEY = (KEY >> NON_FLUSH_BIT_SHIFT);
					next_rank = rankArray[((int) KEY < CIRCUMFERENCE_SEVEN ? (int) KEY
							: (int) KEY - CIRCUMFERENCE_SEVEN)];
					player_rank[n] = next_rank;
				} else {
					// ** This could be a good place to optimize **
					table_flush_key = (table_flush_key == UNVERIFIED ? (deckcardsSuit[table_card_1] == FLUSH_SUIT ? deckcardsFlush[table_card_1]
							: 0)
							+ (deckcardsSuit[table_card_2] == FLUSH_SUIT ? deckcardsFlush[table_card_2]
									: 0)
							+ (deckcardsSuit[table_card_3] == FLUSH_SUIT ? deckcardsFlush[table_card_3]
									: 0)
							+ (deckcardsSuit[table_card_4] == FLUSH_SUIT ? deckcardsFlush[table_card_4]
									: 0)
							: table_flush_key);
					int player_flush_key = table_flush_key
							+ player_flush_stem[n][FLUSH_SUIT];
					next_rank = flushRankArray[player_flush_key];
					player_rank[n] = next_rank;
				}

				// // Compare the player_rank
				//
				// Case: new outright strongest player
				if (current_best_rank < next_rank) {
					// Update current best rank
					current_best_rank = next_rank;
					// Reset number of players interested
					number_of_players_sharing_the_pot = 1;
				} else if (current_best_rank == next_rank) { // Case: pot
																// shared
																// with
																// another
																// player
				// Increment by 1 the number of interest players
					number_of_players_sharing_the_pot++;
				}
				// Last Case: new player loses
				// There is nothing we need to do.
				// /////
				n++;
			} while (n < number_of_players);

			bestrank = -1;
			noPlayers = 0;
			hash = 0;
			for( int p=0; p<number_of_players; p++ ){
				if( bestrank<player_rank[p] ){
					bestrank = player_rank[p];
					noPlayers = 1;
				}else if( bestrank==player_rank[p] ){
					noPlayers++;
				}
			}
			for( int p=0; p<number_of_players; p++ ){
				if(bestrank==player_rank[p]){
					hash = hash*2+1;
				}else{
					hash = hash*2;
				}
			}
			for(n=0;n<num_type;n++){
				if(equity_type[n]==hash){
					break;
				}
			}
			if(n == num_type){
				equity_type[n]=hash;
				equity_count[n]=1;
				for(int p=0; p<number_of_players; p++){
					if(bestrank==player_rank[p]){
						share_amount[n][p]=equity_share[noPlayers];
					}else{
						share_amount[n][p]=0;
					}
				}
				num_type++;
			}else{
				equity_count[n]++;
			}
			total_count++;
			/*
			// Calculate incremental equity
			int incremental_equity = equity_share[number_of_players_sharing_the_pot];

			// Add to the cumulative equities
			int p = 0;
			do {
				if (player_rank[p] == current_best_rank) {
					equity[p] += incremental_equity;
				}
				p++;
			} while (p < number_of_players);
			*/
			i++;
		} while (i < number_of_undealt_cards);

		int j;
		for(i = 0; i < num_type; i++){
			//if(share_amount[i][0]<1)
			//System.out.println(share_amount[i][0]+" "+share_amount[i][1]+" "+equity_count[i]+" "+total_count);
			for(j = 0; j < num_type; j++ ){
				double prob = equity_count[i]*equity_count[j]/((double)total_count*total_count); 
				if(i==0){
					for(int p=0; p<number_of_players; p++){
						equity[p] += prob*share_amount[j][p];
					}
				}else if(j==0){
					for(int p=0; p<number_of_players; p++){
						equity[p] += prob*share_amount[i][p];
					}
				}else{
					for(int p=0; p<number_of_players; p++){
						equity[p] += prob*(share_amount[i][p]+share_amount[j][p])/2;
					}
				}
			}
		}
		
		long total_equity = 0;
		for (i = 0; i < number_of_players; i++) {
			total_equity += equity[i];
		}

		double[] equity_percentage = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0 };

		// int Equity[] = new int[] {EQUITY_TWO, EQUITY_THREE, EQUITY_FOUR,
		// EQUITY_FIVE, EQUITY_SIX, EQUITY_SEVEN, EQUITY_EIGHT};
		// double equity_sum = 0.0;

		for (i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
			equity_percentage[i] = 100 * (equity[i] + 0.0) / total_equity;
			// equity_sum += equity_percentage[i];
		}
		// equity_percentage[player_defect] = 100.0f - equity_sum;
//		String s = String.format("%.8f_%.8f_%.8f_%.8f_%.8f_%.8f_%.8f_%.8f",
//				equity_percentage[0], equity_percentage[1],
//				equity_percentage[2], equity_percentage[3],
//				equity_percentage[4], equity_percentage[5],
//				equity_percentage[6], equity_percentage[7]);

		return equity_percentage;
	}
}