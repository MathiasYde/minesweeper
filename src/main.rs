use rand::Rng;
use yew::prelude::*;

#[derive(Clone, Copy)]
struct Cell {
    bomb: bool,
    flag: bool,
    revealed: bool,
    nearby_bombs: i32,
}

impl Default for Cell {
    fn default() -> Self {
        Cell {
            bomb: false,
            flag: false,
            revealed: false,
            nearby_bombs: 0
        }
    }
}

const SIZE: usize = 8;

fn make_minesweeper_board() -> [Cell; SIZE * SIZE] {
    let mut rng = rand::thread_rng();
    let mut board = [Cell::default(); SIZE * SIZE];

    for i in 0..SIZE*SIZE {
        if rng.gen_bool(0.1) {
            board[i] = Cell {
                bomb: true,
                ..board[i]
            };
        }
    }

    for i in 0..SIZE*SIZE {
        let cell = &board[i];
        if cell.bomb { continue; }

        let mut nearby_bombs = 0;
        board.get(((i as i32) + 1) as usize).map(|cell: &Cell| if cell.bomb { nearby_bombs += 1; });
        board.get(((i as i32) - 1) as usize).map(|cell: &Cell| if cell.bomb { nearby_bombs += 1; });
        board.get(((i as i32) + SIZE as i32) as usize).map(|cell: &Cell| if cell.bomb { nearby_bombs += 1; });
        board.get(((i as i32) - SIZE as i32) as usize).map(|cell: &Cell| if cell.bomb { nearby_bombs += 1; });
        board.get(((i as i32) + SIZE as i32 + 1) as usize).map(|cell: &Cell| if cell.bomb { nearby_bombs += 1; });
        board.get(((i as i32) + SIZE as i32 - 1) as usize).map(|cell: &Cell| if cell.bomb { nearby_bombs += 1; });
        board.get(((i as i32) - SIZE as i32 + 1) as usize).map(|cell: &Cell| if cell.bomb { nearby_bombs += 1; });
        board.get(((i as i32) - SIZE as i32 - 1) as usize).map(|cell: &Cell| if cell.bomb { nearby_bombs += 1; });

        board[i] = Cell {
            nearby_bombs,
            ..board[i]
        };
    }

    return board;
}

#[function_component]
fn Minesweeper() -> Html {
    let board = use_state(|| make_minesweeper_board());

    let on_cell_click = {
        let board = board.clone();

        move |event: MouseEvent, index| {
            event.prevent_default();

            let mut new_board = (*board).clone();
            let cell: &Cell = &new_board[index];

            new_board[index] = match (event.button(), cell.revealed, cell.flag, cell.bomb) {
                (2, false, false, _) => Cell { flag: true, ..*cell },
                (2, false, true, _) => Cell { flag: false, ..*cell },
                // false on cell.flag to prevent accidental clicks
                (0, false, false, _) => Cell { revealed: true, ..*cell },
                _ => *cell,
            };

            board.set(new_board);
        }
    };

    html! {
        <section id="board" class="grid" style={
            format!("\
                grid-template-columns: repeat({SIZE}, minmax(0, 1fr));\
                grid-template-rows: repeat({SIZE}, minmax(0, 1fr));")}>
        {for (0..SIZE*SIZE).map(|index| {
            let cell = &board[index as usize];

            let on_cell_click = on_cell_click.clone();

            html! {
                <button onmouseup={move |event| on_cell_click(event, index)} class="cell aspect-square">{
                    match (cell.bomb, cell.flag, cell.revealed, cell.nearby_bombs) {
                        (_, true, false, _) => html! {"🚩"},
                        (_, _, false, _) => html! {""},
                        (false, _, true, nearby_bombs) => html! {nearby_bombs},
                        (true, _, true, _) => html! {"💣"},
                    }
                }</button>
            }
        })}
        </section>
    }
}

#[function_component]
fn App() -> Html {
    html! {
        <div class="container mx-auto">
            <h1 class="text-4xl font-bold text-center">{"Minesweeper"}</h1>
            <Minesweeper />
        </div>
    }
}

fn main() {
    wasm_logger::init(wasm_logger::Config::default());
    yew::Renderer::<App>::new().render();
}
