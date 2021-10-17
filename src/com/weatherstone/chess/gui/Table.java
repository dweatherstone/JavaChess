package com.weatherstone.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.google.common.collect.Lists;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.MoveTransition;
import com.weatherstone.chess.engine.board.MoveUtils;
import com.weatherstone.chess.engine.pieces.Piece;
import com.weatherstone.chess.engine.player.Player;
import com.weatherstone.chess.engine.player.ai.MiniMax;
import com.weatherstone.chess.engine.player.ai.MoveStrategy;
import com.weatherstone.chess.engine.player.ai.StandardBoardEvaluator;

public class Table extends Observable {

	private final JFrame gameFrame;
	private final BoardPanel boardPanel;
	private final GameHistoryPanel gameHistoryPanel;
	private final TakenPiecesPanel takenPiecesPanel;
	private final MoveLog moveLog;
	private final GameSetup gameSetup;
	private Board chessBoard;
	private Piece sourceTile;
	// private Tile destinationTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;
	private Move computerMove;
	private boolean highlightLegalMoves;
	
	private final Color lightTileColour = Color.decode("#FFFACD");
	private final Color darkTileColour = Color.decode("#593E1A");

	private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
	private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
	private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
	private static String defaultPieceImagesPath = "art/simple/";

	private static final Table INSTANCE = new Table();
	
	private Table() {
		this.gameFrame = new JFrame("DRW Chess");
		this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.gameFrame.setLayout(new BorderLayout());
		final JMenuBar tableMenuBar = createTableMenuBar();
		this.gameFrame.setJMenuBar(tableMenuBar);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
		this.chessBoard = Board.createStandardBoard();
		this.boardPanel = new BoardPanel();
		this.moveLog = new MoveLog();
		this.addObserver(new TableGameAIWatcher());
		this.gameSetup = new GameSetup(this.gameFrame, true);
		this.gameHistoryPanel = new GameHistoryPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.boardDirection = BoardDirection.NORMAL;
		this.highlightLegalMoves = false;
		this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
		this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
		this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
		this.gameFrame.setVisible(true);
	}
	
	public static Table get() {
		return INSTANCE;
	}
	
	private Board getGameBoard() {
		return this.chessBoard;
	}
	
	public void show() {
		Table.get().getMoveLog().clear();
		Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
	}

	private JMenuBar createTableMenuBar() {
		final JMenuBar tableMenuBar = new JMenuBar();
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createPreferencesMenu());
		tableMenuBar.add(createOptionsMenu());
		return tableMenuBar;
	}

	private JMenu createFileMenu() {
		final JMenu fileMenu = new JMenu("File");

		final JMenuItem openPGN = new JMenuItem("Load PGN file");
		openPGN.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Open a PGN file...");

			}
		});
		fileMenu.add(openPGN);
		final JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		return fileMenu;
	}
	
	private JMenu createPreferencesMenu() {
		final JMenu preferencesMenu = new JMenu("Preferences");
		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boardDirection = boardDirection.opposite();
				boardPanel.drawBoard(chessBoard);
			}
		});
		preferencesMenu.add(flipBoardMenuItem);
		preferencesMenu.addSeparator();
		final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
		legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
			}
		});
		preferencesMenu.add(legalMoveHighlighterCheckbox);
		return preferencesMenu;
	}
	
	private JMenu createOptionsMenu() {
		final JMenu optionsMenu = new JMenu("Options");
		final JMenuItem resetMenuItem = new JMenuItem("New Game");
		resetMenuItem.addActionListener(e -> undoAllMoves());
		optionsMenu.add(resetMenuItem);
		
		final JMenuItem evaluateBoardMenuItem = new JMenuItem("Evaluate Board");
		evaluateBoardMenuItem.addActionListener(e -> System.out.println(StandardBoardEvaluator.get().evaluationDetails(chessBoard, gameSetup.getSearchDepth())));
		optionsMenu.add(evaluateBoardMenuItem);
		
		final JMenuItem escapeAnalysis = new JMenuItem("Escape Analysis Score");
		escapeAnalysis.addActionListener(e -> {
			final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
			if (lastMove != null) {
				System.out.println(MoveUtils.exchangeScore(lastMove));
			}
		});
		optionsMenu.add(escapeAnalysis);
		
		final JMenuItem legalMovesMenuItem = new JMenuItem("Current State");
        legalMovesMenuItem.addActionListener(e -> {
            System.out.println(chessBoard.getWhitePieces());
            System.out.println(chessBoard.getBlackPieces());
            System.out.println(playerInfo(chessBoard.currentPlayer()));
            System.out.println(playerInfo(chessBoard.currentPlayer().getOpponent()));
        });
        optionsMenu.add(legalMovesMenuItem);

        final JMenuItem undoMoveMenuItem = new JMenuItem("Undo last move");
        undoMoveMenuItem.addActionListener(e -> {
            if(Table.get().getMoveLog().size() > 0) {
                undoLastMove();
            }
        });
        optionsMenu.add(undoMoveMenuItem);
		
		final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
		setupGameMenuItem.addActionListener(e -> {
			Table.get().getGameSetup().promptUser();
			Table.get().setupUpdate(Table.get().getGameSetup());
		});
		
		optionsMenu.add(setupGameMenuItem);
		
		return optionsMenu;
	}
	
	private void undoLastMove() {
		final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
		this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
		this.computerMove = null;
		Table.get().getMoveLog().removeMove(lastMove);
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(chessBoard);
	}

	private static String playerInfo(final Player player) {
		return ("Player is: " +player.getAlliance() + "\nlegal moves (" +player.getLegalMoves().size()+ ") = " +player.getLegalMoves() + "\ninCheck = " +
                player.isInCheck() + "\nisInCheckMate = " +player.isInCheckMate() +
                "\nisCastled = " +player.isCastled())+ "\n";
	}

	private void undoAllMoves() {
		for (int i = Table.get().getMoveLog().size() - 1; i >= 0; i--) {
			final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
			this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
		}
		this.computerMove = null;
		Table.get().getMoveLog().clear();
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(chessBoard);
	}

	private GameSetup getGameSetup() {
		return this.gameSetup;
	}
	
	private void setupUpdate(final GameSetup gameSetup) {
		setChanged();
		notifyObservers(gameSetup);
	}
	
	private static class TableGameAIWatcher implements Observer {

		@Override
		public void update(final Observable o, final Object arg) {
			if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
					!Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
					!Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
				// create an AI thread
				// execute AI work
				final AIThinkTank thinkTank = new AIThinkTank();
				thinkTank.execute();
			}
			
			if (Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
				// TODO: Change this to show a JOptionPane
				System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer() + " is in Check Mate!");
				JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
						"Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in Check Mate!",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
			
			if (Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
				// TODO: Change this to show a JOptionPane
				System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer() + " is in Stale Mate!");
				JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
						"Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in Stale Mate!",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
	}
	
	public void updateGameBoard(final Board board) {
		this.chessBoard = board;
	}
	

	public void updateComputerMove(Move move) {
		this.computerMove = move;
	}
	
	private MoveLog getMoveLog() {
		return this.moveLog;
	}
	
	private GameHistoryPanel getGameHistoryPanel() {
		return this.gameHistoryPanel;
	}
	
	private TakenPiecesPanel getTakenPiecesPanel() {
		return this.takenPiecesPanel;
	}
	
	private BoardPanel getBoardPanel() {
		return this.boardPanel;
	}
	
	private void moveMadeUpdate(final PlayerType playerType) {
		setChanged();
		notifyObservers(playerType);
	}
	
	enum PlayerType {
		HUMAN,
		COMPUTER
	}
	
	private static class AIThinkTank extends SwingWorker<Move, String> {
		private AIThinkTank() {
			
		}

		@Override
		protected Move doInBackground() throws Exception {
			final MoveStrategy miniMax = new MiniMax(4);
			final Move bestMove = miniMax.execute(Table.get().getGameBoard());
			return bestMove;
		}
		
		@Override
		public void done() {
			try {
				final Move bestMove = get();
				Table.get().updateComputerMove(bestMove);
				Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
				Table.get().getMoveLog().addMove(bestMove);
				Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
				Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
				Table.get().moveMadeUpdate(PlayerType.COMPUTER);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	private class BoardPanel extends JPanel {

		final List<TilePanel> boardTiles;

		BoardPanel() {
			super(new GridLayout(8, 8));
			this.boardTiles = new ArrayList<TilePanel>();
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				final TilePanel tilePanel = new TilePanel(this, i);
				this.boardTiles.add(tilePanel);
				add(tilePanel);
			}
			setPreferredSize(BOARD_PANEL_DIMENSION);
			validate();
		}
		
		public void drawBoard(final Board board) {
			removeAll();
			for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
				tilePanel.drawTile(board);
				add(tilePanel);
			}
			validate();
			repaint();
		}
	}
	
	public static class MoveLog {
		private final List<Move> moves;
		
		MoveLog() {
			this.moves = new ArrayList<>();
		}
		
		public List<Move> getMoves() {
			return this.moves;
		}
		
		public void addMove(final Move move) {
			this.moves.add(move);
		}
		
		public int size() {
			return this.moves.size();
		}
		
		public void clear() {
			this.moves.clear();
		}
		
		public boolean removeMove(final Move move) {
			return this.moves.remove(move);
		}
		
		public Move removeMove(final int index) {
			return this.moves.remove(index);
		}
	}

	private class TilePanel extends JPanel {

		private final int tileId;

		TilePanel(final BoardPanel boardPanel, final int tileId) {
			super(new GridBagLayout());
			this.tileId = tileId;
			setPreferredSize(TILE_PANEL_DIMENSION);
			assignTileColour();
			assignTilePieceIcon(chessBoard);
			addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(final MouseEvent e) {
					
				}
				
				@Override
				public void mousePressed(final MouseEvent e) {
					
				}
				
				@Override
				public void mouseExited(final MouseEvent e) {
					
				}
				
				@Override
				public void mouseEntered(final MouseEvent e) {
					
				}
				
				@Override
				public void mouseClicked(final MouseEvent event) {
					if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) ||
							BoardUtils.isEndGame(Table.get().getGameBoard())) {
						return;
					}
					
					if (SwingUtilities.isRightMouseButton(event)) {
						sourceTile = null;
						humanMovedPiece = null;
					} else if (SwingUtilities.isLeftMouseButton(event)) {
						if (sourceTile == null) {
							// first click
							sourceTile = chessBoard.getPiece(tileId);
							humanMovedPiece = sourceTile;
							if (humanMovedPiece == null) {
								sourceTile = null;
							}
						} else {
							// second click
							final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getPiecePosition(), tileId);
							final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
							if (transition.getMoveStatus().isDone()) {
								chessBoard = transition.getToBoard();
								moveLog.addMove(move);
							}
							sourceTile = null;
							humanMovedPiece = null;
						}
					}
					SwingUtilities.invokeLater(() -> {
						gameHistoryPanel.redo(chessBoard, moveLog);
						takenPiecesPanel.redo(moveLog);
						//if (gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
							Table.get().moveMadeUpdate(PlayerType.HUMAN);
						//}
						boardPanel.drawBoard(chessBoard);
					});
				}
			});
			validate();
		}
		
		public void drawTile(final Board board) {
			assignTileColour();
			assignTilePieceIcon(board);
			highlightLegals(board);
			validate();
			repaint();
		}

		private void assignTileColour() {
			if (BoardUtils.INSTANCE.FIRST_ROW.get(this.tileId) || BoardUtils.INSTANCE.THIRD_ROW.get(this.tileId)
					|| BoardUtils.INSTANCE.FIFTH_ROW.get(this.tileId) || BoardUtils.INSTANCE.SEVENTH_ROW.get(this.tileId)) {
				setBackground(this.tileId % 2 == 0 ? lightTileColour : darkTileColour);
			} else if (BoardUtils.INSTANCE.SECOND_ROW.get(this.tileId) || BoardUtils.INSTANCE.FOURTH_ROW.get(this.tileId)
					|| BoardUtils.INSTANCE.SIXTH_ROW.get(this.tileId) || BoardUtils.INSTANCE.EIGHTH_ROW.get(this.tileId)) {
				setBackground(this.tileId % 2 != 0 ? lightTileColour : darkTileColour);
			}
		}

		private void assignTilePieceIcon(final Board board) {
			this.removeAll();
			if (board.getPiece(this.tileId) != null) {
				try {
					final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath
							+ board.getPiece(this.tileId).getPieceAlliance().toString().substring(0, 1)
							+ board.getPiece(this.tileId).toString() + ".gif"));
					add(new JLabel(new ImageIcon(image)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		private void highlightLegals(final Board board) {
			if (highlightLegalMoves) {
				for (final Move move : pieceLegalMoves(board)) {
					if (move.getDestinationCoordinate() == this.tileId) {
						try {
							add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private Collection<Move> pieceLegalMoves(final Board board) {
			if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
				final List<Move> pieceMoves = new ArrayList<>();
				for (final Move move : board.currentPlayer().getLegalMoves()) {
					if (move.getMovedPiece() == humanMovedPiece) {
						pieceMoves.add(move);
					}
				}
				
				return pieceMoves;
			}
			return Collections.emptyList();
		}

	}
	
	public enum BoardDirection {
		
		NORMAL {

			@Override
			List<TilePanel> traverse(List<TilePanel> boardTiles) {
				return boardTiles;
			}

			@Override
			BoardDirection opposite() {
				return FLIPPED;
			}
			
		},
		FLIPPED {

			@Override
			List<TilePanel> traverse(List<TilePanel> boardTiles) {
				return Lists.reverse(boardTiles);
			}

			@Override
			BoardDirection opposite() {
				return NORMAL;
			}
			
		};
		
		abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
		abstract BoardDirection opposite();
	}

}
