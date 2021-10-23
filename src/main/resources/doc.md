

ACsGuis permet d'utiliser des feuilles de style CSS pour styliser les guis Minecraft.

Le gui peut être créé soit en utilisant du code java, avec une API basée sur celle de Reden, soit en utilisant Sqript.

- Voir des exemples de guis codés en java [lien github](https://github.com/AymericBdy/ACsGuis/tree/master/src/main/java/fr/aym/acsguis/test).
- [Tutoriel Sqript](sqript_doc.md)

Pour ce qui est du style, les propriétés CSS les plus courantes sont supportées, avec toutefois beaucoup de différentes avec le CSS officiel. Ainsi le placement des composants se fait uniquement avec les propriétés "left, right, top et bottom", ou avec les layouts (lien).

-> Voir la liste des propriétés CSS supportées en bas de ce document.

### Support des couleurs : 

Toutes les couleurs css sont supportées ("red", "orange", "darkgreen"...), ainsi que le rgb (`rgb(red, green, blue)`), le rgba (rgba(red, green, blue, alpha)) et l'hexadécimal (#000000 à #FFFFFF).

### Héritage des propriétés :

Certaines propriétés sont automatiquement héritées par les éléments fils d'un composant : si on déclare color: red; sur un Panel, alors tous les Label de ce Panel seront rouges par défaut. Ces propriétés sont précisées dans le tableau plus bas.

### Auto style :

!!!warning
	Disponible uniquement dans les guis java pour le moment. Une alternative est de modifier l'id ou la class de l'élément directement.

Il est possible d'appliquer un style dynamique aux éléments css, pour modifier facilement la couleur d'un élément, par exemple, sans changer son id ou sa class. Tout se fait avec les AutoStyleHandler.

<Partie non terminée, allez voir la javadoc>

### Customisation des polices d'écriture :

Il est possible d'utiliser des polices d'écritures custom, au format .ttf.

Il faut pour cela créer un bloc @font-face :

````css
@font-face {
    font-family: "dnx:test_font";
    /*font-style: italic;*/
    src: url("acsguis:css/font2.ttf");
    font-size: 52px;
}
````

| Nom         | Description                                                  |
| ----------- | ------------------------------------------------------------ |
| font-family | Nom de la police d'écriture                                  |
| src         | Emplacement du fichier .ttf (ce doit être une resource location) |
| font-style  | Style de la police d'écriture (seul 'italic' est supporté pour le moment) |
| font-size   | Taille de la police d'écriture                               |



### Liste des propritétés CSS supportées :

Voici la liste des propriétés CSS supportées par ACsGuis. Toutes les propriétés non mentionnées ne seront pas reconnues, mais n'hésitez pas à me faire des suggestions pour en supporter d'autres.

| Nom                     | Description                                                  | Exemples                  |
| ----------------------- | ------------------------------------------------------------ | ------------------------- |
| background-color        | Couleur de l'arrière-plan                                    | background-color: orange; |
| background-image        | Texture de l'arrière-plan                                    | TODO                      |
| background-repeat       | Répétion de la texture de l'arrière plan                     | Voir doc officielle css   |
| color                   | Couleur du texte                                             | color: rgb(30, 150, 80);  |
| visibility              | Visibilité de composant : "hidden" le cachera, sinon il sera toujours visible | visibility: hidden;       |
| font-size               | Taille du texte                                              | font-size: 15px;          |
| font-style              | Style du texte ('italic' ou 'normal')                        |                           |
| font-family             | Police d'écriture, voir l'utilisation de polices d'écriture custom |                           |
| border-color            | Couleur des bordures                                         |                           |
| border-width            | Largeur des bordures                                         | border-width: 2px;        |
| border-position         | Position des bordures ('internal' ou 'external')             |                           |
| border-radius           | Rayon des bordures, pour faire des bords arrondis            | border-radius: 5px;       |
| text-shadow             | 'enable' : Active l'ombre du texte                           |                           |
| z-index                 | Position du composant en profondeur, sans unité              | z-index: 50;              |
| padding-left            | Marge à gauche                                               |                           |
| padding-top             | Marche en haut                                               |                           |
| padding-right           | Marge à droit                                                |                           |
| paddin-bottom           | Marge en bas                                                 |                           |
| text-align              | Alignement horizontal du texte ('left', 'right' ou 'center') |                           |
| text-align-vertical     | Alignement vertical du texte ('top', 'bottom' ou 'center')   |                           |
| component-layout        | Layout du composant, voir plus haut                          |                           |
| width                   | Largeur du composant                                         |                           |
| max-width               | Largeur maximum du composant                                 |                           |
| min-width               | Largeur minimum du composant                                 |                           |
| height                  | Hauteur du composant                                         |                           |
| max-height              | Hauteur maximale du composant                                |                           |
| min-height              | Hauteur minimale du composant                                |                           |
| left                    | Position à gauche du composant                               | Voir plus haut            |
| right                   | Position à droite du composant                               | Voir plus haut            |
| top                     | Position en haut du composant                                | Voir plus haut            |
| bottom                  | Position en bas du composant                                 | Voir plus haut            |
| horizontal-position     | 'center' pour mettre le composant au centre horizontalement  |                           |
| vertical-position       | 'center' pour mettre le composant au centre verticalement    |                           |
| progress-bar-full-image | Image de fond d'une barre de progression pleine              | Voir 'background-image'   |
| progress-bar-full-color | Couleur de fond d'une barre de progression pleine            | Voir 'background-color'   |
| progress-bar-text-color | Couleur du texte d'une barre de progression                  | Voir 'color'              |