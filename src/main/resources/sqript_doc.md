ACsGuis permet de facilement créer des guis dans Minecraft, en voici un tutoriel complet.

!!!danger
	Vous devez savoir utiliser Sqript.

Commencez par créer un nouveau fichier sqript puis suivez les étapes !

## Définir le gui :

Commencez par déclarer un block de gui : `define gui frame nomDuGui:` où 'nomDuGui' est le nom de votre gui, utilisé pour l'afficher.

Comme votre gui utilisera du css pour son style, vous devez le lier à votre gui : ajoutez une field "css_sheets" à votre gui frame :

```
define gui frame nomDuGui:
	css_sheets: ["acsguis:css/dnx_debug.css"]
```

"acsguis:css/dnx_debug.css" est l'emplacement du fichier css, et vous pouvez en mettre plusieurs (séparés par des ",").

!!!info
	Vous pouvez également ajouter des fields "css_class", "css_id" et "css_code" pour modifier respectivement la classe css, l'id css ou le code css du gui frame.

!!!danger
	Si vous voulez que votre gui ressemble à quelque chose, il vous faudra maîtriser au moins les bases du css, voici un [tutoriel](https://developer.mozilla.org/fr/docs/Learn/Getting_started_with_the_web/CSS_basics).

 Après avoir déclaré les fields, vous pouvez utiliser des instructions sqript normal, comme afficher des choses dans la console ou modifier les valeurs de certaines variables.

### Ajouter des composants au gui :

Il y a deux manière d'ajouter des composants : avec un block sqript, ou avec une action.

Le block permet de déclarer un panel avec des sous-composants, alors que l'action est réservée à des composants simple (type label ou button) sans sous-composants.

!!!info
	Il est possible d'utiliser des conditions if et des boucles (for, while...) dans la structure du gui, pour simplifier le script et/ou rendre le gui plus dynamique.

#### Ajouter un composant simple :

L'instruction à écrire est de la forme :

`add css component label with id "reload_models" and class "reload_button" and text "Recharger les packs"`

!!!info
	Les propriétés "id", "class" et "text" sont facultatives, vous auriez pu écrire `add css component label`

- "id" est l'id css du composant, qui permet d'être identifié "#reload_models" dans le code css.
- "class" est la classe css du composant, qui permet d'être identifié ".reload_button" dans le code css.
- "text" est le texte du composant, cela ne marche que pour ceux pouvant afficher du texte.

#### Ajouter un container de composants :

Les container de composants permettent d'ajouter des composants dans un composant :

L'instruction à écrire est de la forme :

`add css component panel with id "general" and class "test":`

!!!info
	Comme avant l'id et la class sont facultatifs. En général un seul suffit pour faire le style du composant.

!!!warning
	C'est un bloc, donc les composants "fils" devront être indentés d'un tab de plus.

Vous pourrez ensuite ajouter d'autres composants (composants simples et containers) dans celui-ci !

### Liste des composants supportés :

Voici tous les composants supportés actuellement dans l'addon sqript d'ACsGuis :

| Nom                   | Type      | Description                                                  |
| --------------------- | --------- | ------------------------------------------------------------ |
| panel                 | Container | Container de composants le plus simple                       |
| tabbed_pane           | Container | Container de composants constitué de plusieurs panels (tabs). Les panels ajoutés à l'intérieur sont nommés avec une variable de type String "next_tab_pane_name" à modifier (voir le code d'exemple) |
| scroll_pane           | Container | Container de composants scrollable                           |
| label                 | Simple    | Texte                                                        |
| text_field            | Simple    | Champ de texte                                               |
| text_area             | Simple    | Champ de texte sur plusieurs lignes                          |
| password_field        | Simple    | Champ de texte cachant les caractères écrits                 |
| integer_field         | Simple    | Champ de texte n'acceptant que des chiffres (entiers)        |
| checkbox              | Simple    | Case à cocher (avec texte associé)                           |
| button                | Simple    | Bouton (avec  texte)                                         |
| entity_render         | Simple    | Rendu d'entité (comme le joueur dans l'inventaire)           |
| combo_box             | Simple    | Liste de choix                                               |
| progress_bar          | Simple    | Barre de progression (horizontale)                           |
| progress_bar_vertical | Simple    | Barre de progression (verticale)                             |

### Modifier les propriétés des composants (panel) :

Certains composants peuvent avoir des propriétés customisables (le minimum et le maximum d'un integer_field par exemple), voici comment faire :

!!!warning
	Le composant doit être déclaré en tant que block (comme un container de composants), même si c'est un composant simple.

Chaque propriété custom a une variable associée qui est chargée dans le block associé au composant, par exemple il est possible de modifier le texte d'un composant : 

```
add css component label with id "t1" and text "Loading":
   set text to "Un texte random"
```

Ici, "text" est la variable contenant le texte du label.

Tous les composants possèdent la variable "style" permettant d'écrire du code css à injecter dans le composant, et tous les composants pouvant contenir du texte possèdent la variable "text". 

Certains composants possèdent des variables en plus :

| Composant               | Variables              | Type Sqript |
| ----------------------- | ---------------------- | ----------- |
| Tous                    | style                  | String      |
| Textuels                | text                   | String      |
| Panels                  | layout                 | Layout      |
| Panels sauf tabbed_pane | next_tab_pane_name     | String      |
| integer_field           | min_value et max_value | Number      |
| checkbox                | checked_state          | Boolean     |
| entity_render           | entity_to_render       | Entity      |
| combo_box               | combo_choices          | StringArray |
| progress_bar(_vertical) | bar_progress           | Number      |

TODO : Get la valeur d'un composant (d'une field).

### Mettre facilement en page un container de composants :

Vous avez peut-être remarqué l'existence d'une variable de type Layout sur les panels, celle-ci permet de placer automatiquement les composants fils.

Pour mettre un layout de grille sur un panel il suffit de faire :

```
set layout to new grid layout with size [-1,25] spacing 5 direction "horizontal" elements per line 1
```

- [-1, 25] est la taille (largeur, hauteur) des composants fils du panel. -1 signifie que la largeur des composants sera égale à 100% de celle du panel "père". 25 signifie que les composants "fils" feront 25 pixels de haut.
- 5 corresponde à l'espace en pixels entre chaque composant (dans les deux directions).
- "horizontal" correspond à la direction de la grille, cela permet de d'abord remplir la première ligne puis descendre sur le ligne suivante et recommencer. "vertical" est aussi supporté pour faire colonne par colonne.
- 1 correspond au nombre d'éléments par ligne (ou colonne).

### Les évènements de composants :

Il est possible d'écouter les évènements (clics, survols...) affectant n'importe quel composant.

!!!warning
	Le composant doit être déclaré en tant que block (comme un container de composants), même si c'est un composant simple.

Il suffit d'ajouter, dans le block du composant, un block `on component <nom de l'event>` : 

```
on component click:
   print "Vie du joueur"
   print [player's hunger]
```

Ici, la vie du joueur sera affichée en cliquant sur le composant.

Il est alors possible de modifier les propriétés du composant en utilisant la variable `this_component` correspondant au composant cible de l'event (ici, du clic) : `set text of this_component to "Cliqué"` modifiera le texte du bouton pour afficher "Cliqué" lorsque l'on clique dessus.

D'autres variables liées à l'évènement sont aussi accessibles.

Voici la liste des évènements de composants supportés :

| Nom                               | Description                                                  | Variables                                                    |
| --------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| on component click                | Appelé en cliquant sur le composant                          | click_x, click_y, click_button                               |
| on component close                | Appelé quand le gui est fermé                                |                                                              |
| on component mouse click advanced | Appelé en cliquant sur le composant, à plusieurs étapes du clic | click_x, click_y, click_button, type (double_click, mouse_press, mouse_release) |
| on component focus                | Appelé quand l'élément est sélectionné/désélectionné         | type (focus, focus_loose)                                    |
| on component key input            | Appelé quand l'élément a le focus et qu'une touche est appuyée | typed_char, key_code                                         |
| on component mouse move           | Appelé quand la souris survole le composant                  | mouse_x, mouse_y, type (mouse_moved, mouse_hover, mouse_unhover) |
| on component mouse wheel          | Appelé en scrollant dans l'élément                           | dWheel                                                       |
| on component tick                 | Appelé à chaque tick                                         |                                                              |
| on component render background    | Appelé après le rendu de l'arrière plan du composant         | render_x, render_y, render_width, render_height              |
| on component render foreground    | Appelé après le rendu du premier plan du composant           | render_x, render_y, render_width, render_height              |



## Afficher le gui :

Il faut d'abord register toutes les feuilles de style css utilisées dans l'event `on acsguis load` afin qu'elles soient préchargées au lancement du jeu :

```
on acsguis load:
   register css sheet "sample:css/test.css"
```

Vous pouvez en mettre autant que vous voulez.

Ensuite, pour afficher le gui utilisez simplement `display css gui "nomDuGui"` à n'importe quel endroit de votre sqript (ça doit être dans le même fichier que là où est défini le gui). Attention : laissez-bien les guillemets.

!!!warning
	Un gui ne peut-être affiché que sur un client (un joueur), le serveur, lui, ne sait même pas ce que c'est, alors faites attention à ce que `display css gui` ne soit jamais appelé côté serveur !

## Sqript source :

Un sqript d'exemple est disponible [ici](https://github.com/AymericBdy/ACsGuis/blob/master/run/scripts/sample/test_gui.sq).