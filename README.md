📄 Sección para la Documentación: Reglas y Mecánica del Juego
1. Descripción General del Juego

NutriBattle (o el nombre de tu proyecto) es un juego de cartas digital diseñado con el objetivo pedagógico de concientizar a un público adolescente sobre el valor nutricional de los alimentos. A través de la comparación directa entre Alimentos Sanos (Héroes) y Alimentos Procesados (Villanos/Zombies), el juego demuestra que los alimentos saludables son la mejor elección para el rendimiento, la digestión y el bienestar general.
2. Flujo de Pantallas y Registro de Usuario

    Registro Inicial: Antes de comenzar la partida, el jugador debe ingresar su Nombre y Edad. Estos datos permiten contextualizar la experiencia y segmentar a la audiencia objetivo.

    Registro de Ranking (Fin de Partida): Al concluir las contiendas, el jugador selecciona tres iniciales (ejemplo: FJB) para almacenar su puntaje final en la base de datos local (Room / SQLite) y figurar en la pantalla de Leaderboard o Ranking.

3. Mecánica de la Contienda y Reglas de Juego

El enfrentamiento se desarrolla bajo una modalidad de 3 contiendas (turnos) entre el Jugador y la Inteligencia Artificial (Máquina):

    Fase de Mezclado: Al iniciar, el mazo completo se mezcla con animaciones visuales para garantizar la aleatoriedad de las cartas asignadas a cada bando.

    Fase de Colocación (Boca Abajo): En cada turno, el jugador y la máquina arrojan una carta sobre el escenario de juego (el mantel). Las cartas permanecen boca abajo durante un temporizador en regresiva de 5 segundos.

    Fase de Revelación: Cumplidos los 5 segundos, se activa un efecto de giro (card flip animation) que revela de forma simultánea ambas cartas.

4. Criterios de Evaluación y Atributos de Puntuación

La victoria en cada contienda se determina mediante el análisis numérico de los tres atributos nutricionales de las cartas:

    Energía (0–100): Evalúa la calidad y duración del aporte calórico (energía sostenida vs. picos efímeros de azúcar).

    Saciedad (0–100): Mide la capacidad del alimento para mantener satisfecho al organismo (fibra y proteína vs. digestión veloz y antojos).

    Vitalidad (0–100 / HP): Representa la densidad de nutrientes, antioxidantes y el impacto positivo en la salud intestinal.

Determinación del Ganador:

    Ganador de la Contienda: Se realiza la suma o comparación directa punto por punto de los atributos de ambas cartas. La carta que obtenga el balance superior en los parámetros gana el turno. Por diseño pedagógico, los alimentos sanos poseen combinaciones de atributos superiores frente a los procesados.

    Ganador Definitivo: Tras finalizar las 3 contiendas, el sistema calcula el cómputo global de rondas ganadas y determina la victoria final de la partida, asignando el puntaje que será guardado junto a las siglas del jugador en la base de datos SQLite.
