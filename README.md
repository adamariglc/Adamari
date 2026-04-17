En esta practica, terminamos el pomodoro, completando la parte de los botones para resetear y skipear, tambien agrege una frase motivacional, hice una paleta de
colores.
Tambien me costo un poco declarar la libreria de google, por que no sabia o no recordaba bien en donde era y me habia atorado un poco en eso.
Igual segun yo trate de poner el logo de la "empresa" pero creo que no se ve reflejada.
Declaramos elementos en el MainActivity de botones y de textView, que son los estados de sesion, y cuando ya este completado, y terminamos los botones de reset y skip.
Y a los botones ya tienen acciones, detiene y reinicia el temporizador, y actualiza la pantalla.
SkiptoNextSession, simula que ya termino y pasa a la siguiente sesion.

¿Cuál fue el mayor reto al gestionar el CountDownTimer y cómo evitaste que se crearan múltiples instancias al presionar el botón repetidamente?
Primero se valida antes de iniciar, es decir, di ya esta ejecutandose no se crea otro timer, solo se pausa.
Y sino se cancela antes de crear otro, para que de esta manera solo exista un timer activo y se elimina el anterior.
Tambien usamos el uso de una sola instancia. 

¿Por qué es preferible usar un LinearLayout con addView para los puntos de progreso en lugar de declarar 4 ImageViews estáticos en el XML?
Por que de esta manera se van agregando con forme avanza la aplicacion, por ejemplo si yo nada mas hago dos sesiones, solo las dos se veran reflejadas, en lugar de las 4 con solo 2 activas
por asi decirlo. Y de la misma manera si quiero hacer seis sesiones los cuatro estaticos me limitarian y pues se rompe la logica.

Si quisiéramos añadir una función para que el usuario personalice sus propios tiempos de enfoque, ¿qué parte de tu lógica actual tendría que cambiar y cómo lo abordarías?
Pues primero los elementos no serian estaticos, para que el usuario lopueda modificar. Tambien se deberia de modificar el resetModeTime(), que es en el que se le asigna el tiempo a cada sesion.
Lo abordaria creando botones con diferentes tiempos, como 5, 10, 15 minutos, o sino seria una barra con el que hubiera un maximo de tiempo y el usuario arrastraria hasta el tiempo que el desea.
Pero no se como se haria eso.

¿Cómo harían para que el tiempo del temporizador se mantenga si el usuario minimiza la app?
Se guaradria el tiempo en el que se deberia terminar, se crearia una nueva variable, para que de esta manera cuando se minimize pueda regresar a un estado, con un tiempo, siendo este calculado con 
el tiempo en el que esta y en el que se deberia de terminar


Con el cambio que hice, fue que se agregara un punto cada vez que este en FOCUS y aumenta el contador (solo hay 3 puntos por que en el 4 se reinicia, borra los puntos y cambia a REST) esto en onSessionFinished. Y ahora el addDot solo crea la bolita.

López Cortes Adamari Gianina
320268458

Practica 3:
En esta practica lo que hicimos fue agregar la base de datos para que se guarde el historial de sesiones del uruario.
En AndroidManifest, lo que hacemos es pedir requisitos del celular para nuestra aplicacion, y pues agregamos nuestras vistas.
Y en los strings tenemos la configuracion del idioma, tanto en español como en ingles, por eso hay dos strings, en estos en la parte para contar las sessiones, usamos un plural, por que pues pueden ser varias sesiones, usando "one" y "other".
En SessionHistoryActivity, en bindViews() cramos el SessionManager y en sessionManager = new SessionManager(this) conectamos con la base de datos. 
List<Session> sessions = sessionManager.getAllSessions() obtenemos todas las sesiones que esten en la base de datos y las recuperamos en una lista. 
setupRecyclerView() creamos el adapter, y definimos como mostrar los elementos que se hizo verticalmente. 
layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
Si no hay sesiones se muestra mensaje vacio y se oculta lista, y si, si hay entonces muestra la lista y oculta el mensaje.

En SessionManager tecnicamente es como la base de datos, ya que en esta se crean, se guardan y consultan las sesiones. 
En filtrar por dias lo que hace es consultar con el mismo foramto con el que se guardo que es "EEE, dd MMM yyyy". Mientras que en el filtro por semana no se puede realizar esa comparacion, entonces lo que hicimos fue crear una lista por dias List<String> daysOfCurrentWeek y luego realizamos la consulta.

En SessionHistoryAdapter lo que hace es tomar la lista de sessiones y reciclamos las vistas para no gastar memoria. ViewHolder es el contenedor de vistas, cada fila tiene Tipo (Focus / Break), fecha, hora, duración y estado. 

Y en el MainActivity lo que hace es el flujo de la aplicacion, es decir, se crea sesión, empieza timer, termina onSessionFinished(), se guarda en SQLite, cambia modo y la UI se actualiza

Lo que más me costo trabajo fue que eran muchos archivos, y pues algunos (AndroidManifest) no los puse en donde iban y con eso no me dejaba compilar, y en el build tenia que fijarme que todo estuviera bien y pues en general en cada uno de los archivos, y en especial el orden. Fueron muchas cosas al mismo tiempo y me confundi un poco. Igual creo que otra cosa que se me complico, fue hacer lo de por semana, por que no se puede hacer la comparacion e hice la lsita de dias y ya de esta forma se logro realizar la consulta.

Pues algo que haria diferente, es ponerle dibujitos o animaciones quizas, en cuestion de funcionalidades, tal vez que se pueda configurar el tiempo, como si quisieras estudiar por mas tiempo o si quieres tomar un descanso más largo.
