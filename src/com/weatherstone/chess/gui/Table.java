package com.weatherstone.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import com.google.common.collect.Lists;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.MoveTransition;
import com.weatherstone.chess.engine.board.MoveUtils;
import com.weatherstone.chess.engine.board.Move.MoveFactory;
import com.weatherstone.chess.engine.pieces.Piece;
import com.weatherstone.chess.engine.player.Player;
import com.weatherstone.chess.engine.player.ai.AlphaBetaPruning;
import com.weatherstone.chess.engine.player.ai.StandardBoardEvaluator;
import com.weatherstone.chess.pgn.FenUltils;
import com.weatherstone.chess.pgn.MySqlGamePersistence;
import com.weatherstone.chess.pgn.PGNUtils;

public class Table extends Observable {

	private final JFrame gameFrame;
	private final GameHistoryPanel gameHistoryPanel;
	private final TakenPiecesPanel takenPiecesPanel;
	private final DebugPanel debugPanel;
	private final BoardPanel boardPanel;
	private final MoveLog moveLog;
	private final GameSetup gameSetup;
	private Board chessBoard;
	private Move computerMove;
	private Piece sourceTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;
	private String pieceIconPath;
	private boolean highlightLegalMoves;
	private boolean useBook;
	private Color lightTileColour = Color.decode("#FFFACD");
	private Color darkTileColour = Color.decode("#593E1A");

	private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
	private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
	private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

	private static final Table INSTANCE = new Table();
	
	private Table() {
		this.gameFrame = new JFrame("DRW Chess");
		final JMenuBar tableMenuBar = new JMenuBar();
		populateMenuBar(tableMenuBar);
		this.gameFrame.setJMenuBar(tableMenuBar);
		this.gameFrame.setLayout(new BorderLayout());
		this.chessBoard = Board.createStandardBoard();
		this.boardDirection = BoardDirection.NORMAL;
		this.highlightLegalMoves = false;
		this.useBook = false;
		this.pieceIconPath = "art/simple/";
		this.gameHistoryPanel = new GameHistoryPanel();
		this.debugPanel = new DebugPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.boardPanel = new BoardPanel();
		this.moveLog = new MoveLog();
		this.addObserver(new TableGameAIWatcher());
		this.gameSetup = new GameSetup(this.gameFrame, true);
		this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
		this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
		this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
		this.gameFrame.add(this.debugPanel, BorderLayout.SOUTH);
		javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
		this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
		centre(this.gameFrame);
		this.gameFrame.setVisible(true);
	}
	
	public static Table get() {
		return INSTANCE;
	}
	
	private JFrame getGameFrame() {
		return this.gameFrame;
	}
	
	private Board getGameBoard() {
		return this.chessBoard;
	}

	private MoveLog getMoveLog() {
		return this.moveLog;
	}

	private BoardPanel getBoardPanel() {
		return this.boardPanel;
	}

	private GameHistoryPanel getGameHistoryPanel() {
		return this.gameHistoryPanel;
	}

	private TakenPiecesPanel getTakenPiecesPanel() {
		return this.takenPiecesPanel;
	}
	
	private DebugPanel getDebugPanel() {
		return this.debugPanel;
	}

	private GameSetup getGameSetup() {
		return this.gameSetup;
	}
	
	private boolean getHighlightLegalMoves() {
		return this.highlightLegalMoves;
	}
	
	private boolean getUseBook() {
		return this.useBook;
	}

	public void show() {
		Table.get().getMoveLog().clear();
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
		Table.get().getDebugPanel().redo();
	}
	
	private void populateMenuBar(final JMenuBar tableMenuBar) {
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createPreferencesMenu());
		tableMenuBar.add(createOptionsMenu());
	}
	
	private static void centre(final JFrame frame) {
		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		final int w = frame.getSize().width;
		final int h = frame.getSize().height;
		final int x = (dim.width - w) / 2;
		final int y = (dim.height - h) / 2;
		frame.setLocation(x, y);
	}

	private JMenu createFileMenu() {
		final JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		final JMenuItem openPGN = new JMenuItem("Load PGN file", KeyEvent.VK_O);
		openPGN.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			int option = chooser.showOpenDialog(Table.get().getGameFrame());
			if (option == JFileChooser.APPROVE_OPTION) {
				loadPGNFile(chooser.getSelectedFile());
			}
		});
		fileMenu.add(openPGN);
		
		final JMenuItem openFEN = new JMenuItem("Load FEN file", KeyEvent.VK_F);
		openFEN.addActionListener(e -> {
			String fenString = JOptionPane.showInputDialog("Input FEN");
			if (fenString != null) {
				undoAllMoves();
				chessBoard = FenUltils.createGameFromFEN(fenString);
				Table.get().getBoardPanel().drawBoard(chessBoard);
			}
		});
		fileMenu.add(openFEN);
		
		final JMenuItem saveToPGN = new JMenuItem("Save Game", KeyEvent.VK_S);
		saveToPGN.addActionListener(e -> {
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return ".pgn";
				}
				@Override
				public boolean accept(File file) {
					return file.isDirectory() || file.getName().toLowerCase().endsWith("pgn");
				}
			});
			final int option = chooser.showSaveDialog(Table.get().getGameFrame());
			if (option == JFileChooser.APPROVE_OPTION) {
				savePGNFile(chooser.getSelectedFile());
			}
		});
		fileMenu.add(saveToPGN);
		
		final JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitMenuItem.addActionListener(e -> {
			Table.get().getGameFrame().dispose();
			System.exit(0);
		});
		fileMenu.add(exitMenuItem);
		
		return fileMenu;
	}
	
	private JMenu createOptionsMenu() {
		final JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		
		final JMenuItem resetMenuItem = new JMenuItem("New Game", KeyEvent.VK_P);
		resetMenuItem.addActionListener(e -> undoAllMoves());
		optionsMenu.add(resetMenuItem);
		
		final JMenuItem evaluateBoardMenuItem = new JMenuItem("Evaluate Board", KeyEvent.VK_E);
		evaluateBoardMenuItem.addActionListener(e -> System.out.println(StandardBoardEvaluator.get().evaluationDetails(chessBoard, gameSetup.getSearchDepth())));
		optionsMenu.add(evaluateBoardMenuItem);
		
		final JMenuItem escapeAnalysis = new JMenuItem("Escape Analysis Score", KeyEvent.VK_S);
		escapeAnalysis.addActionListener(e -> {
			final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
			if (lastMove != null) {
				System.out.println(MoveUtils.exchangeScore(lastMove));
			}
		});
		optionsMenu.add(escapeAnalysis);
		
		final JMenuItem legalMovesMenuItem = new JMenuItem("Current State", KeyEvent.VK_L);
	    legalMovesMenuItem.addActionListener(e -> {
	        System.out.println(chessBoard.getWhitePieces());
	        System.out.println(chessBoard.getBlackPieces());
	        System.out.println(playerInfo(chessBoard.currentPlayer()));
	        System.out.println(playerInfo(chessBoard.currentPlayer().getOpponent()));
	    });
	    optionsMenu.add(legalMovesMenuItem);
	
	    final JMenuItem undoMoveMenuItem = new JMenuItem("Undo last move", KeyEvent.VK_M);
	    undoMoveMenuItem.addActionListener(e -> {
	        if(Table.get().getMoveLog().size() > 0) {
	            undoLastMove();
	        }
	    });
	    optionsMenu.add(undoMoveMenuItem);
		
		final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game", KeyEvent.VK_S);
		setupGameMenuItem.addActionListener(e -> {
			Table.get().getGameSetup().promptUser();
			Table.get().setupUpdate(Table.get().getGameSetup());
		});
		optionsMenu.add(setupGameMenuItem);
		
		return optionsMenu;
	}

	private JMenu createPreferencesMenu() {
		final JMenu preferencesMenu = new JMenu("Preferences");
		preferencesMenu.setMnemonic(KeyEvent.VK_P);
		
		final JMenu colourChooserSubMenu = new JMenu("Choose Colours");
		colourChooserSubMenu.setMnemonic(KeyEvent.VK_S);
		
		final JMenuItem chooseDarkMenuItem = new JMenuItem("Choose Dark Tile Colour");
		colourChooserSubMenu.add(chooseDarkMenuItem);
		
		final JMenuItem chooseLightMenuItem = new JMenuItem("Choose Light Tile Colour");
		colourChooserSubMenu.add(chooseLightMenuItem);
		
		final JMenuItem chooseLegalHighlightMenuItem = new JMenuItem("Choose Legal Move Highlight Colour");
		colourChooserSubMenu.add(chooseLegalHighlightMenuItem);
		
		preferencesMenu.add(colourChooserSubMenu);
		
		chooseDarkMenuItem.addActionListener(e -> {
			final Color colourChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Dark Tile Colour", Table.get().getGameFrame().getBackground());
			if (colourChoice != null) {
				Table.get().getBoardPanel().setTileDarkColour(chessBoard, colourChoice);
			}
		});
		
		chooseLightMenuItem.addActionListener(e -> {
			final Color colourChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Light Tile Colour", Table.get().getGameFrame().getBackground());
			if (colourChoice != null) {
				Table.get().getBoardPanel().setTileLightColour(chessBoard, colourChoice);
			}
		});
		
		final JMenu chessMenChoiceSubMenu = new JMenu("Choose Chess Men Image Set");
		
		final JMenuItem holyWarriorsMenuItem = new JMenuItem("Holy Warriors");
		chessMenChoiceSubMenu.add(holyWarriorsMenuItem);

        final JMenuItem abstractMenMenuItem = new JMenuItem("Abstract Men");
        chessMenChoiceSubMenu.add(abstractMenMenuItem);

        final JMenuItem fancyMenMenuItem = new JMenuItem("Fancy Men");
        chessMenChoiceSubMenu.add(fancyMenMenuItem);

        final JMenuItem fancyMenMenuItem2 = new JMenuItem("Fancy Men 2");
        chessMenChoiceSubMenu.add(fancyMenMenuItem2);
        
		holyWarriorsMenuItem.addActionListener(e -> {
			pieceIconPath = "art/holywarriors/";
			Table.get().getBoardPanel().drawBoard(chessBoard);
		});
		
		abstractMenMenuItem.addActionListener(e -> {
			pieceIconPath = "art/simple/";
			Table.get().getBoardPanel().drawBoard(chessBoard);
		});
		
		fancyMenMenuItem.addActionListener(e -> {
			pieceIconPath = "art/fancy/";
			Table.get().getBoardPanel().drawBoard(chessBoard);
		});
		
		fancyMenMenuItem2.addActionListener(e -> {
			pieceIconPath = "art/fancy2/";
			Table.get().getBoardPanel().drawBoard(chessBoard);
		});
		
		preferencesMenu.add(chessMenChoiceSubMenu);
		
		chooseLegalHighlightMenuItem.addActionListener(e -> {
			System.out.println("Implement me!");
			Table.get().getGameFrame().repaint();
		});
		
		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(e -> {
			boardDirection = boardDirection.opposite();
			boardPanel.drawBoard(chessBoard);
		});
		preferencesMenu.add(flipBoardMenuItem);
		preferencesMenu.addSeparator();
		
		final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
		legalMoveHighlighterCheckbox.addActionListener(e -> highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected());
		preferencesMenu.add(legalMoveHighlighterCheckbox);
		
		final JCheckBoxMenuItem useBookMovesCheckbox = new JCheckBoxMenuItem("Use Book Moves", false);
		useBookMovesCheckbox.addActionListener(e -> useBook = useBookMovesCheckbox.isSelected());
		preferencesMenu.add(useBookMovesCheckbox);
		
		return preferencesMenu;
	}
	
	private static String playerInfo(final Player player) {
		return ("Player is: " +player.getAlliance() + "\nlegal moves (" +player.getLegalMoves().size()+ ") = " +player.getLegalMoves() + "\ninCheck = " +
	            player.isInCheck() + "\nisInCheckMate = " +player.isInCheckMate() +
	            "\nisCastled = " +player.isCastled())+ "\n";
	}

	public void updateGameBoard(final Board board) {
		this.chessBoard = board;
	}

	public void updateComputerMove(Move move) {
		this.computerMove = move;
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
		Table.get().getDebugPanel().redo();
	}
	
	private static void loadPGNFile(final File pgnFile) {
		try {
			PGNUtils.persistPGNFile(pgnFile);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void savePGNFile(final File pgnFile) {
		try {
			PGNUtils.writeGameToPGNFile(pgnFile, Table.get().getMoveLog());
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void undoLastMove() {
		final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
		this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
		this.computerMove = null;
		Table.get().getMoveLog().removeMove(lastMove);
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(chessBoard);
		Table.get().getDebugPanel().redo();
	}

	private void moveMadeUpdate(final PlayerType playerType) {
		setChanged();
		notifyObservers(playerType);
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
				System.out.println(Table.get().getGameBoard().currentPlayer() + " is set to AI, thinking...");
				final AIThinkTank thinkTank = new AIThinkTank();
				thinkTank.execute();
			}
			
			if (Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
				System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer() + " is in Check Mate!");
				JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
						"Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in Check Mate!",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
			
			if (Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
				System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer() + " is in Stale Mate!");
				JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
						"Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in Stale Mate!",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
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
			final Move bestMove;
			final Move bookMove = Table.get().getUseBook()
					? MySqlGamePersistence.get().getNextBestMove(Table.get().getGameBoard(), Table.get().getGameBoard().currentPlayer(), Table.get().getMoveLog().getMoves().toString().replaceAll("\\[", "").replaceAll("\\]", ""))
					: MoveFactory.getNullMove();
			if (Table.get().getUseBook() && bookMove != MoveFactory.getNullMove()) {
				bestMove = bookMove;
			}
			else {
				final AlphaBetaPruning strategy = new AlphaBetaPruning(Table.get().getGameSetup().getSearchDepth());
				strategy.addObserver(Table.get().getDebugPanel());
				bestMove = strategy.execute(Table.get().getGameBoard());
			}
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
				Table.get().getDebugPanel().redo();
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
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setBackground(Color.decode("#8B4726"));
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
		
		void setTileDarkColour(final Board board, final Color darkColour) {
			for (final TilePanel boardTile : boardTiles) {
				boardTile.setDarkTileColour(darkColour);
			}
			drawBoard(board);
		}
		
		void setTileLightColour(final Board board, final Color lightColour) {
			for (final TilePanel boardTile : boardTiles) {
				boardTile.setLightTileColour(lightColour);
			}
			drawBoard(board);
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
			highlightTileBorder(chessBoard);
			addMouseListener(new MouseListener() {
				
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
						debugPanel.redo();
					});
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					
				}

				@Override
				public void mouseEntered(final MouseEvent e) {
					
				}

				@Override
				public void mouseReleased(final MouseEvent e) {
					
				}
				
				@Override
				public void mousePressed(final MouseEvent e) {
					
				}
			});
			validate();
		}

		public void drawTile(final Board board) {
			assignTileColour();
			assignTilePieceIcon(board);
			highlightTileBorder(board);
			highlightLegals(board);
			highlightAIMove();
			validate();
			repaint();
		}
		
		public void setLightTileColour(Color lightColour) {
			lightTileColour = lightColour;
			
		}

		public void setDarkTileColour(Color darkColour) {
			darkTileColour = darkColour;
		}
		
		private void highlightTileBorder(final Board board) {
			if (humanMovedPiece != null &&
					humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance() &&
					humanMovedPiece.getPiecePosition() == this.tileId) {
				setBorder(BorderFactory.createLineBorder(Color.cyan));
			} else {
				setBorder(BorderFactory.createLineBorder(Color.GRAY));
			}
		}
		
		private void highlightAIMove() {
			if (computerMove != null) {
				if (this.tileId == computerMove.getCurrentCoordinate()) {
					setBackground(Color.pink);
				} else if (this.tileId == computerMove.getDestinationCoordinate()) {
					setBackground(Color.red);
				}
			}
		}
		
		private void highlightLegals(final Board board) {
			if (Table.get().getHighlightLegalMoves()) {
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
				return humanMovedPiece.calculateLegalMoves(board);
			}
			return Collections.emptyList();
		}
		
		private void assignTilePieceIcon(final Board board) {
			this.removeAll();
			if (board.getPiece(this.tileId) != null) {
				try {
					final BufferedImage image = ImageIO.read(new File(pieceIconPath
							+ board.getPiece(this.tileId).getPieceAlliance().toString().substring(0, 1)
							+ board.getPiece(this.tileId).toString() + ".gif"));
					add(new JLabel(new ImageIcon(image)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
		private void assignTileColour() {
			if (BoardUtils.INSTANCE.FIRST_ROW.get(this.tileId) || 
				BoardUtils.INSTANCE.THIRD_ROW.get(this.tileId) || 
				BoardUtils.INSTANCE.FIFTH_ROW.get(this.tileId) || 
				BoardUtils.INSTANCE.SEVENTH_ROW.get(this.tileId)) {
				setBackground(this.tileId % 2 == 0 ? lightTileColour : darkTileColour);
			} else if (BoardUtils.INSTANCE.SECOND_ROW.get(this.tileId) || 
					   BoardUtils.INSTANCE.FOURTH_ROW.get(this.tileId) || 
					   BoardUtils.INSTANCE.SIXTH_ROW.get(this.tileId) || 
					   BoardUtils.INSTANCE.EIGHTH_ROW.get(this.tileId)) {
				setBackground(this.tileId % 2 != 0 ? lightTileColour : darkTileColour);
			}
		}
	
	}

}
