# Documentación del Proyecto:

**Grupo 2**  
* **Betancor Fernando**  
* **Rey Lucas**  
* **Rodríguez Stephany**  
* **Scodelari Julia**  

---

## 📑 Índice de Contenidos
(poner aquí el índice)

---

## A. Descripción del Juego

### Nombre del juego
Guerra de platillos

### Objetivo
Concientizar al público adolescente sobre el valor nutricional de los alimentos, mediante la comparación directa entre alimentos sanos (representados por héroes) y alimentos procesados (representados por villanos o zombies).
El juego demuestra que los alimentos saludables con la mejor elección para el rendimiento, la digestión y el bienestar general.

### Público destinatario y Persona Usuaria
Adolescentes de entre 13 y 14 años
(poner aquí la persona usuaria)

#### User Persona:
(poner aquí el user persona)

### Mecánica principal
El enfrentamiento se desarrolla bajo una modalidad de 3 contiendas (turnos) entre el Jugador y la Inteligencia Artificial (Máquina). Consta de tres fases:

### Forma de interacción del jugador
El jugador ingresará a la aplicación, y la primera vez se le pedirá un **Registro Inicial**, en el cual se le pedirá ingresar su nombre, alias por el cual se lo nombrará en el ranking global, edad, sexo y contraseña.
Luego ingresará al menú principal, donde podrá iniciar una partida (cuyo recorrido se encuentra en el apartado a continuación), acceder a su perfil, consultar el ranking global entre todos los jugadores, y cerrar sesión.

### Recorrido general de la partida
* **Fase de Mezclado:** Al iniciar, el mazo completo se mezcla con animaciones visuales para garantizar la aleatoriedad de las cartas asignadas a cada bando.
* **Fase de Colocación (boca abajo):** En cada turno, el jugador y la máquina arrojan una carta sobre el escenario de juego (el mantel). Las cartas permanecen boca abajo durante un temporizador en regresiva de 5 segundos.
* **Fase de Revelación:** Cumplidos los 5 segundos, se activa un efecto de giro (card flip animation) que revela de forma simultánea ambas cartas. Este efecto puede activarse con un botón en pantalla o agitando el teléfono (indicándole en pantalla al jugador que es posible hacer este gesto). Al agitar el teléfono, se ejecutará dicha acción y el mismo vibrará, ya que hacer gestos con el dispositivo deben estar acompañados de una respuesta inmediata, para una experiencia óptima.

Las tres fases se realizarán 3 veces (habrá **3 contiendas**), lo que constituye una **mano**, de manera similar a una mano de Truco, y el **criterio para determinar la carta ganadora** en cada contienda es mediante los tres atributos nutricionales de las cartas:

* **Energía (0–100):** Evalúa la calidad y duración del aporte calórico (energía sostenida vs. picos efímeros de azúcar).
* **Saciedad (0–100):** Mide la capacidad del alimento para mantener satisfecho al organismo (fibra y proteína vs. digestión veloz y antojos).
* **Vitalidad (0–100 / HP):** Representa la densidad de nutrientes, antioxidantes y el impacto positivo en la salud intestinal.

El ganador se determina de la siguiente manera:
* **Ganador de la contienda**: Se realiza la suma o comparación directa punto por punto de los atributos de ambas cartas. La carta que obtenga el balance superior en los parámetros gana el turno. Por diseño pedagógico, los alimentos sanos poseen combinaciones de atributos superiores frente a los procesados.
* **Ganador de la mano**: Tras finalizar las 3 contiendas, el sistema calcula el cómputo global de rondas ganadas y determina la victoria final de la partida, asignando el puntaje que será guardado junto a las iniciales del jugador en la base de datos SQLite.

---

## B. Estructura de la Aplicación

### Esquema conceptual del flujo
(insertar aquí el esquema conceptual)

### Diagrama de navegación entre pantallas
(insertar aquí el diagrama)

### Descripción detallada de pantallas y sus relaciones
(poner aquí la descripción)

---

## C. Prototipo y Design System

### Enlaces a Prototipos y Diseño en Figma
* **Figma con ideas de diseños:** 
* **Figma con flujo de pantallas:** 

### Decisiones visuales: Paleta de Colores y Accesibilidad

#### Paleta para Protanopia / Deuteranopia

| Color original | Equivalente | Uso |
|:-:|:-:|:--|

#### Paleta para Tritanopia

| Color original | Equivalente | Uso |
|:-:|:-:|:--|

Link a la paleta de colores: 

### Tipografías y Jerarquía de Texto


### Componentes de Interfaz: Botones


### Recursos gratuitos usados como inspiración


### Estrategia de Navegación entre Pantallas



---

## D. Implementación Técnica

### Arquitectura MVVM

### Administración del estado del juego

### Ciclo de vida de las Activities

### Recursos multimedia incorporados

### Librerías y componentes de Android utilizados

---

## E. Participación de los Integrantes

| Integrante | Rol en el equipo | Tareas realizadas |
| :--- | :--- | :--- |
| **Betancor Fernando** | AAA | AAA |
| **Rey Lucas** | AAA | AAA |
| **Rodríguez Stephany** | AAA | AAA |
| **Scodelari Julia** | AAA | AAA |
