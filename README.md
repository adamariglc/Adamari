En esta practica 1 lo que hicimos fue configurar dos dispositivos un celular y una tableta, para que cuando estemos trabajando en el desarrollo de la aplicacion podamos ver como se ve en los dispositivos. 
Tambien lo que hicimos fue crear un "error" en github con un compañero, para recordar comandos de git, el error se basa en que dos personas editen la misma linea del codigo y cuando se haga el commit entonces 
lo tengamos que resolver a mano. 
Por ultimo hicimos una clase llamada TaskManager para agregar, leer, actualizar y eliminar (CRUD) utilizando ArrayList

No tuve problemas con la aceleracion del hardware, ya que por eso pedi prestada una computadora para no tener problemas, y no tuve problemas con la creacion de AVD tanto del celular como el de la tableta, 
siguiendo las instrucciones de la clase y de la tarea.

Use ArrayList por que es mas facil manipular los elementos de la lista, pues va creciendo conforme van metiendo objetos.

Si las tareas se guardaran en un servidor remoto, ¿qué cambiaría en el manejo de excepciones de tu función?
  Pues primero necesitaria acceso a internet para hacer lo de CRUD, y pues de esta manera no estaria lanzando un error, o no le estraia notificando al usuario.
  Deberia de manejar respuestas del servidor 
  Tambien tardaria mucho, tendria mucha latencia cada vez que se haga un llamada al servidor.
  
