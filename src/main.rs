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

    // place bombs
    for i in 0..SIZE*SIZE {
        if rng.gen_bool(0.1) {
            board[i] = Cell {
                bomb: true,
                ..board[i]
            };
        }
    }

    for i in 0..(SIZE * SIZE) {
        let x = i % SIZE;
        let y = i / SIZE;

        if x < 0 || x >= SIZE { continue; }
        if y < 0 || y >= SIZE { continue; }

        println!("x: {}, y: {}", x, y);
    }

    board
}

#[function_component]
fn Minesweeper() -> Html {
    let board = use_state(|| make_minesweeper_board());
    let flag_count = use_state(|| board.iter().filter(|cell| cell.bomb).count() as i32);

    let on_cell_click = {
        let board = board.clone();
        let flag_count = flag_count.clone();

        move |event: MouseEvent, index| {
            event.prevent_default();

            let mut new_board = (*board).clone();
            let cell: &Cell = &new_board[index];

            new_board[index] = match (event.button(), cell.revealed, cell.flag, cell.bomb, *flag_count > 0) {
                (2, false, false, _, true) => {
                    flag_count.set(*flag_count - 1);
                    Cell { flag: true, ..*cell }
                }
                (2, false, true, _, _) => {
                    flag_count.set(*flag_count + 1);
                    Cell { flag: false, ..*cell }
                },
                // false on cell.flag to prevent accidental clicks
                (0, false, false, _, _) => Cell { revealed: true, ..*cell },
                _ => *cell,
            };

            board.set(new_board);
        }
    };

    html! {
        <div class="flex flex-col h-screen">
            <section id="control" class="flex flex-row h-24 p-4">
                {format!("Flags: {}", *flag_count)}
            </section>
            <section id="board" class="grid object-scale-down w-64" style={
                format!("\
                    grid-template-columns: repeat({SIZE}, minmax(0, 1fr));\
                    grid-template-rows: repeat({SIZE}, minmax(0, 1fr));")}>
            {for (0..SIZE*SIZE).map(|index| {
                let cell = &board[index as usize];
                let on_cell_click = on_cell_click.clone();

                html! {
                    <button
                        onmouseup={move |event| on_cell_click(event, index)}
                        class="cell aspect-square">
                            {match (cell.bomb, cell.flag, cell.revealed, cell.nearby_bombs) {
                                (_, true, false, _) => html! {"🚩"},
                                (_, _, false, _) => html! {""},
                                (false, _, true, nearby_bombs) => match nearby_bombs {
                                    0 => html! {<p class="text-gray-600">{"0"}</p>},
                                    1 => html! {<p class="text-blue-600">{"1"}</p>},
                                    2 => html! {<p class="text-green-600">{"2"}</p>},
                                    3 => html! {<p class="text-red-600">{"3"}</p>},
                                    4 => html! {<p class="text-purple-600">{"4"}</p>},
                                    5 => html! {<p class="text-red-800">{"5"}</p>},
                                    6 => html! {<p class="text-cyan-600">{"6"}</p>},
                                    7 => html! {<p class="text-black">{"7"}</p>},
                                    8 => html! {<p class="text-gray-600">{"8"}</p>},
                                    _ => panic!("Invalid nearby_bombs value: {}", nearby_bombs),
                                },
                                (true, _, true, _) => html! {"💣"},
                            }
                    }</button>
                }
            })}
            </section>
        </div>
    }
}

#[function_component]
fn App() -> Html {
    html! {
        <div class="container mx-auto">
            <h1 class="text-4xl font-bold text-center">{"Minesweeper"}</h1>
            <Minesweeper />
            <footer>
                <p class="text-center">{"Made with Rust"}</p>
            </footer>
        </div>
    }
}

fn main() {
    wasm_logger::init(wasm_logger::Config::default());
    yew::Renderer::<App>::new().render();
}
