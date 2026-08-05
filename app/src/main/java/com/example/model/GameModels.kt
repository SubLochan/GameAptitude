package com.example.model

enum class GameType(
    val title: String,
    val subtitle: String,
    val durationText: String,
    val roundsText: String,
    val iconName: String,
    val skillsMeasured: String
) {
    GRID_CHALLENGE(
        title = "Grid Challenge",
        subtitle = "Remember dot sequences interrupted by symmetry tests",
        durationText = "9 Minutes",
        roundsText = "Variable Levels",
        iconName = "grid_view",
        skillsMeasured = "Executive attention & working memory"
    ),
    MOTION_CHALLENGE(
        title = "Motion Challenge",
        subtitle = "Slide blocks to pave a clear path for the red ball to the black hole",
        durationText = "6 Minutes",
        roundsText = "10 Levels",
        iconName = "open_with",
        skillsMeasured = "Planning & spatial problem-solving"
    ),
    INDUCTIVE_LOGIC(
        title = "Inductive Logical Thinking",
        subtitle = "Identify pair of grids following the same geometric transformation rule",
        durationText = "5 Minutes",
        roundsText = "10 Questions",
        iconName = "pattern",
        skillsMeasured = "Pattern recognition & rule induction"
    ),
    DEDUCTIVE_LOGIC(
        title = "Deductive Logical Thinking",
        subtitle = "Sudoku-style grid: each symbol appears once per row and column",
        durationText = "6 Minutes",
        roundsText = "10 Questions",
        iconName = "grid_on",
        skillsMeasured = "Logical deduction & constraint satisfaction"
    ),
    NUM_BUBBLES(
        title = "NumBubbles",
        subtitle = "Pop bubbles containing equations that evaluate to the Target number",
        durationText = "12 sec / target",
        roundsText = "10 Rounds",
        iconName = "bubble_chart",
        skillsMeasured = "Speed mental arithmetic"
    ),
    SHORT_CUTS(
        title = "Short Cuts",
        subtitle = "Guide marble along weighted graph path considering red & bonus nodes",
        durationText = "4 Minutes",
        roundsText = "7 Rounds",
        iconName = "alt_route",
        skillsMeasured = "Path optimization & strategic planning"
    ),
    RESEMBLE(
        title = "Resemble",
        subtitle = "Recreate geometric target patterns using rotated tile pieces",
        durationText = "3 Minutes",
        roundsText = "9 Rounds",
        iconName = "rotate_right",
        skillsMeasured = "Mental rotation & spatial visual assembly"
    ),
    TALLY_UP(
        title = "Tally Up",
        subtitle = "Compare side-by-side box sums with multipliers & strikethroughs",
        durationText = "4 sec / question",
        roundsText = "35 Rounds",
        iconName = "compare_arrows",
        skillsMeasured = "Rapid mathematical judgment under pressure"
    ),
    NON_VERBAL_REASONING(
        title = "Non-Verbal Reasoning",
        subtitle = "Mirror & Water images, Venn diagrams, figure completion, series",
        durationText = "8 Minutes",
        roundsText = "12 Questions",
        iconName = "flip",
        skillsMeasured = "Visual abstract reasoning & spatial awareness"
    ),
    CRYPT_ARITHMETIC(
        title = "Crypt Arithmetic Aptitude",
        subtitle = "Decode alphabet substitution math equations (0-9 unique digits)",
        durationText = "6 Minutes",
        roundsText = "5 Puzzles",
        iconName = "pin",
        skillsMeasured = "Algebraic logic & quantitative puzzle solving"
    )
}

enum class ScreenState {
    MENU,
    INSTRUCTIONS,
    PLAYING,
    RESULT
}

data class GameScore(
    val gameType: GameType,
    val score: Int,
    val totalRounds: Int,
    val maxScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)
