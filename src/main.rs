use yew::prelude::*;

#[function_component]
fn Minesweeper() -> Html {
    html! {
        <div></div>
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
