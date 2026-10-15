package com.kaikunxp.pokeshowdown.util

import java.util.concurrent.ConcurrentHashMap

object Pokedex {

    private val dynamicSpeciesTypes = ConcurrentHashMap<String, List<String>>()

    fun updateFromApi(data: Map<String, List<String>>) {
        dynamicSpeciesTypes.putAll(data)
    }

    private val speciesTypes: Map<String, List<String>> = mapOf(
        // Gen 1
        "bulbasaur" to listOf("Grass", "Poison"), "ivysaur" to listOf("Grass", "Poison"), "venusaur" to listOf("Grass", "Poison"),
        "charmander" to listOf("Fire"), "charmeleon" to listOf("Fire"), "charizard" to listOf("Fire", "Flying"),
        "squirtle" to listOf("Water"), "wartortle" to listOf("Water"), "blastoise" to listOf("Water"),
        "caterpie" to listOf("Bug"), "metapod" to listOf("Bug"), "butterfree" to listOf("Bug", "Flying"),
        "weedle" to listOf("Bug", "Poison"), "kakuna" to listOf("Bug", "Poison"), "beedrill" to listOf("Bug", "Poison"),
        "pidgey" to listOf("Normal", "Flying"), "pidgeotto" to listOf("Normal", "Flying"), "pidgeot" to listOf("Normal", "Flying"),
        "rattata" to listOf("Normal"), "raticate" to listOf("Normal"),
        "spearow" to listOf("Normal", "Flying"), "fearow" to listOf("Normal", "Flying"),
        "ekans" to listOf("Poison"), "arbok" to listOf("Poison"),
        "pikachu" to listOf("Electric"), "raichu" to listOf("Electric"), "raichualola" to listOf("Electric", "Psychic"),
        "sandshrew" to listOf("Ground"), "sandslash" to listOf("Ground"),
        "nidoranf" to listOf("Poison"), "nidorina" to listOf("Poison"), "nidoqueen" to listOf("Poison", "Ground"),
        "nidoranm" to listOf("Poison"), "nidorino" to listOf("Poison"), "nidoking" to listOf("Poison", "Ground"),
        "clefairy" to listOf("Fairy"), "clefable" to listOf("Fairy"),
        "vulpix" to listOf("Fire"), "ninetales" to listOf("Fire"), "ninetalesalola" to listOf("Ice", "Fairy"),
        "jigglypuff" to listOf("Normal", "Fairy"), "wigglytuff" to listOf("Normal", "Fairy"),
        "zubat" to listOf("Poison", "Flying"), "golbat" to listOf("Poison", "Flying"),
        "oddish" to listOf("Grass", "Poison"), "gloom" to listOf("Grass", "Poison"), "vileplume" to listOf("Grass", "Poison"),
        "paras" to listOf("Bug", "Grass"), "parasect" to listOf("Bug", "Grass"),
        "venonat" to listOf("Bug", "Poison"), "venomoth" to listOf("Bug", "Poison"),
        "diglett" to listOf("Ground"), "dugtrio" to listOf("Ground"),
        "meowth" to listOf("Normal"), "persian" to listOf("Normal"),
        "psyduck" to listOf("Water"), "golduck" to listOf("Water"),
        "mankey" to listOf("Fighting"), "primeape" to listOf("Fighting"),
        "growlithe" to listOf("Fire"), "arcanine" to listOf("Fire"), "arcaninehisui" to listOf("Fire", "Rock"),
        "poliwag" to listOf("Water"), "poliwhirl" to listOf("Water"), "poliwrath" to listOf("Water", "Fighting"),
        "abra" to listOf("Psychic"), "kadabra" to listOf("Psychic"), "alakazam" to listOf("Psychic"),
        "machop" to listOf("Fighting"), "machoke" to listOf("Fighting"), "machamp" to listOf("Fighting"),
        "bellsprout" to listOf("Grass", "Poison"), "weepinbell" to listOf("Grass", "Poison"), "victreebel" to listOf("Grass", "Poison"),
        "tentacool" to listOf("Water", "Poison"), "tentacruel" to listOf("Water", "Poison"),
        "geodude" to listOf("Rock", "Ground"), "graveler" to listOf("Rock", "Ground"), "golem" to listOf("Rock", "Ground"),
        "ponyta" to listOf("Fire"), "rapidash" to listOf("Fire"),
        "slowpoke" to listOf("Water", "Psychic"), "slowbro" to listOf("Water", "Psychic"), "slowking" to listOf("Water", "Psychic"),
        "magnemite" to listOf("Electric", "Steel"), "magneton" to listOf("Electric", "Steel"),
        "farfetchd" to listOf("Normal", "Flying"),
        "doduo" to listOf("Normal", "Flying"), "dodrio" to listOf("Normal", "Flying"),
        "seel" to listOf("Water"), "dewgong" to listOf("Water", "Ice"),
        "grimer" to listOf("Poison"), "muk" to listOf("Poison"),
        "shellder" to listOf("Water"), "cloyster" to listOf("Water", "Ice"),
        "gastly" to listOf("Ghost", "Poison"), "haunter" to listOf("Ghost", "Poison"), "gengar" to listOf("Ghost", "Poison"),
        "onix" to listOf("Rock", "Ground"),
        "drowzee" to listOf("Psychic"), "hypno" to listOf("Psychic"),
        "krabby" to listOf("Water"), "kingler" to listOf("Water"),
        "voltorb" to listOf("Electric"), "electrode" to listOf("Electric"),
        "exeggcute" to listOf("Grass", "Psychic"), "exeggutor" to listOf("Grass", "Psychic"),
        "cubone" to listOf("Ground"), "marowak" to listOf("Ground"), "marowakalola" to listOf("Fire", "Ghost"),
        "hitmonlee" to listOf("Fighting"), "hitmonchan" to listOf("Fighting"),
        "lickitung" to listOf("Normal"),
        "koffing" to listOf("Poison"), "weezing" to listOf("Poison"), "weezinggalar" to listOf("Poison", "Fairy"),
        "rhyhorn" to listOf("Ground", "Rock"), "rhydon" to listOf("Ground", "Rock"),
        "chansey" to listOf("Normal"),
        "tangela" to listOf("Grass"),
        "kangaskhan" to listOf("Normal"),
        "horsea" to listOf("Water"), "seadra" to listOf("Water"),
        "goldeen" to listOf("Water"), "seaking" to listOf("Water"),
        "staryu" to listOf("Water"), "starmie" to listOf("Water", "Psychic"),
        "mrmime" to listOf("Psychic", "Fairy"),
        "scyther" to listOf("Bug", "Flying"),
        "jynx" to listOf("Ice", "Psychic"),
        "electabuzz" to listOf("Electric"),
        "magmar" to listOf("Fire"),
        "pinsir" to listOf("Bug"),
        "tauros" to listOf("Normal"), "taurospaldeacombat" to listOf("Fighting"), "taurospaldeablaze" to listOf("Fighting", "Fire"), "taurospaldeaaqua" to listOf("Fighting", "Water"),
        "magikarp" to listOf("Water"), "gyarados" to listOf("Water", "Flying"),
        "lapras" to listOf("Water", "Ice"),
        "ditto" to listOf("Normal"),
        "eevee" to listOf("Normal"), "vaporeon" to listOf("Water"), "jolteon" to listOf("Electric"), "flareon" to listOf("Fire"),
        "porygon" to listOf("Normal"),
        "omanyte" to listOf("Rock", "Water"), "omastar" to listOf("Rock", "Water"),
        "kabuto" to listOf("Rock", "Water"), "kabutops" to listOf("Rock", "Water"),
        "aerodactyl" to listOf("Rock", "Flying"),
        "snorlax" to listOf("Normal"),
        "articuno" to listOf("Ice", "Flying"), "zapdos" to listOf("Electric", "Flying"), "moltres" to listOf("Fire", "Flying"),
        "dratini" to listOf("Dragon"), "dragonair" to listOf("Dragon"), "dragonite" to listOf("Dragon", "Flying"),
        "mewtwo" to listOf("Psychic"), "mew" to listOf("Psychic"),

        // Gen 2
        "chikorita" to listOf("Grass"), "bayleef" to listOf("Grass"), "meganium" to listOf("Grass"),
        "cyndaquil" to listOf("Fire"), "quilava" to listOf("Fire"), "typhlosion" to listOf("Fire"), "typhlosionhisui" to listOf("Fire", "Ghost"),
        "totodile" to listOf("Water"), "croconaw" to listOf("Water"), "feraligatr" to listOf("Water"),
        "sentret" to listOf("Normal"), "furret" to listOf("Normal"),
        "hoothoot" to listOf("Normal", "Flying"), "noctowl" to listOf("Normal", "Flying"),
        "ledyba" to listOf("Bug", "Flying"), "ledian" to listOf("Bug", "Flying"),
        "spinarak" to listOf("Bug", "Poison"), "ariados" to listOf("Bug", "Poison"),
        "crobat" to listOf("Poison", "Flying"),
        "chinchou" to listOf("Water", "Electric"), "lanturn" to listOf("Water", "Electric"),
        "pichu" to listOf("Electric"), "cleffa" to listOf("Fairy"), "igglybuff" to listOf("Normal", "Fairy"),
        "togepi" to listOf("Fairy"), "togetic" to listOf("Fairy", "Flying"),
        "natu" to listOf("Psychic", "Flying"), "xatu" to listOf("Psychic", "Flying"),
        "mareep" to listOf("Electric"), "flaaffy" to listOf("Electric"), "ampharos" to listOf("Electric"),
        "bellossom" to listOf("Grass"),
        "marill" to listOf("Water", "Fairy"), "azumarill" to listOf("Water", "Fairy"),
        "sudowoodo" to listOf("Rock"),
        "politoed" to listOf("Water"),
        "hoppip" to listOf("Grass", "Flying"), "skiploom" to listOf("Grass", "Flying"), "jumpluff" to listOf("Grass", "Flying"),
        "aipom" to listOf("Normal"),
        "sunkern" to listOf("Grass"), "sunflora" to listOf("Grass"),
        "yanma" to listOf("Bug", "Flying"),
        "wooper" to listOf("Water", "Ground"), "quagsire" to listOf("Water", "Ground"),
        "espeon" to listOf("Psychic"), "umbreon" to listOf("Dark"),
        "murkrow" to listOf("Dark", "Flying"),
        "misdreavus" to listOf("Ghost"),
        "unown" to listOf("Psychic"),
        "wobbuffet" to listOf("Psychic"),
        "girafarig" to listOf("Normal", "Psychic"),
        "pineco" to listOf("Bug"), "forretress" to listOf("Bug", "Steel"),
        "dunsparce" to listOf("Normal"),
        "gligar" to listOf("Ground", "Flying"),
        "steelix" to listOf("Steel", "Ground"),
        "snubbull" to listOf("Fairy"), "granbull" to listOf("Fairy"),
        "qwilfish" to listOf("Water", "Poison"), "qwilfishhisui" to listOf("Dark", "Poison"),
        "scizor" to listOf("Bug", "Steel"),
        "shuckle" to listOf("Bug", "Rock"),
        "heracross" to listOf("Bug", "Fighting"),
        "sneasel" to listOf("Dark", "Ice"), "sneaselhisui" to listOf("Fighting", "Poison"),
        "teddiursa" to listOf("Normal"), "ursaring" to listOf("Normal"),
        "slugma" to listOf("Fire"), "magcargo" to listOf("Fire", "Rock"),
        "swinub" to listOf("Ice", "Ground"), "piloswine" to listOf("Ice", "Ground"),
        "corsola" to listOf("Water", "Rock"),
        "remoraid" to listOf("Water"), "octillery" to listOf("Water"),
        "delibird" to listOf("Ice", "Flying"),
        "mantine" to listOf("Water", "Flying"),
        "skarmory" to listOf("Steel", "Flying"),
        "houndour" to listOf("Dark", "Fire"), "houndoom" to listOf("Dark", "Fire"),
        "kingdra" to listOf("Water", "Dragon"),
        "phanpy" to listOf("Ground"), "donphan" to listOf("Ground"),
        "porygon2" to listOf("Normal"),
        "stantler" to listOf("Normal"),
        "smeargle" to listOf("Normal"),
        "tyrogue" to listOf("Fighting"), "hitmontop" to listOf("Fighting"),
        "smoochum" to listOf("Ice", "Psychic"), "elekid" to listOf("Electric"), "magby" to listOf("Fire"),
        "miltank" to listOf("Normal"),
        "blissey" to listOf("Normal"),
        "raikou" to listOf("Electric"), "entei" to listOf("Fire"), "suicune" to listOf("Water"),
        "larvitar" to listOf("Rock", "Ground"), "pupitar" to listOf("Rock", "Ground"), "tyranitar" to listOf("Rock", "Dark"),
        "lugia" to listOf("Psychic", "Flying"), "hooh" to listOf("Fire", "Flying"), "celebi" to listOf("Psychic", "Grass"),

        // Gen 3
        "treecko" to listOf("Grass"), "grovyle" to listOf("Grass"), "sceptile" to listOf("Grass"),
        "torchic" to listOf("Fire"), "combusken" to listOf("Fire", "Fighting"), "blaziken" to listOf("Fire", "Fighting"),
        "mudkip" to listOf("Water"), "marshtomp" to listOf("Water", "Ground"), "swampert" to listOf("Water", "Ground"),
        "poochyena" to listOf("Dark"), "mightyena" to listOf("Dark"),
        "zigzagoon" to listOf("Normal"), "linoone" to listOf("Normal"),
        "beautifly" to listOf("Bug", "Flying"), "dustox" to listOf("Bug", "Poison"),
        "lotad" to listOf("Water", "Grass"), "lombre" to listOf("Water", "Grass"), "ludicolo" to listOf("Water", "Grass"),
        "seedot" to listOf("Grass"), "nuzleaf" to listOf("Grass", "Dark"), "shiftry" to listOf("Grass", "Dark"),
        "taillow" to listOf("Normal", "Flying"), "swellow" to listOf("Normal", "Flying"),
        "wingull" to listOf("Water", "Flying"), "pelipper" to listOf("Water", "Flying"),
        "ralts" to listOf("Psychic", "Fairy"), "kirlia" to listOf("Psychic", "Fairy"), "gardevoir" to listOf("Psychic", "Fairy"), "gallade" to listOf("Psychic", "Fighting"),
        "surskit" to listOf("Bug", "Water"), "masquerain" to listOf("Bug", "Flying"),
        "shroomish" to listOf("Grass"), "breloom" to listOf("Grass", "Fighting"),
        "slakoth" to listOf("Normal"), "vigoroth" to listOf("Normal"), "slaking" to listOf("Normal"),
        "nincada" to listOf("Bug", "Ground"), "ninjask" to listOf("Bug", "Flying"), "shedinja" to listOf("Bug", "Ghost"),
        "whismur" to listOf("Normal"), "loudred" to listOf("Normal"), "exploud" to listOf("Normal"),
        "makuhita" to listOf("Fighting"), "hariyama" to listOf("Fighting"),
        "azurill" to listOf("Normal", "Fairy"),
        "nosepass" to listOf("Rock"),
        "skitty" to listOf("Normal"), "delcatty" to listOf("Normal"),
        "sableye" to listOf("Dark", "Ghost"), "mawile" to listOf("Steel", "Fairy"),
        "aron" to listOf("Steel", "Rock"), "lairon" to listOf("Steel", "Rock"), "aggron" to listOf("Steel", "Rock"),
        "meditite" to listOf("Fighting", "Psychic"), "medicham" to listOf("Fighting", "Psychic"),
        "electrike" to listOf("Electric"), "manectric" to listOf("Electric"),
        "plusle" to listOf("Electric"), "minun" to listOf("Electric"),
        "volbeat" to listOf("Bug"), "illumise" to listOf("Bug"),
        "roselia" to listOf("Grass", "Poison"),
        "gulpin" to listOf("Poison"), "swalot" to listOf("Poison"),
        "carvanha" to listOf("Water", "Dark"), "sharpedo" to listOf("Water", "Dark"),
        "wailmer" to listOf("Water"), "wailord" to listOf("Water"),
        "numel" to listOf("Fire", "Ground"), "camerupt" to listOf("Fire", "Ground"),
        "torkoal" to listOf("Fire"),
        "spoink" to listOf("Psychic"), "grumpig" to listOf("Psychic"),
        "spinda" to listOf("Normal"),
        "trapinch" to listOf("Ground"), "vibrava" to listOf("Ground", "Dragon"), "flygon" to listOf("Ground", "Dragon"),
        "cacnea" to listOf("Grass"), "cacturne" to listOf("Grass", "Dark"),
        "swablu" to listOf("Normal", "Flying"), "altaria" to listOf("Dragon", "Flying"),
        "zangoose" to listOf("Normal"), "seviper" to listOf("Poison"),
        "lunatone" to listOf("Rock", "Psychic"), "solrock" to listOf("Rock", "Psychic"),
        "barboach" to listOf("Water", "Ground"), "whiscash" to listOf("Water", "Ground"), "corphish" to listOf("Water"), "crawdaunt" to listOf("Water", "Dark"),
        "baltoy" to listOf("Ground", "Psychic"), "claydol" to listOf("Ground", "Psychic"),
        "lileep" to listOf("Rock", "Grass"), "cradily" to listOf("Rock", "Grass"),
        "anorith" to listOf("Rock", "Bug"), "armaldo" to listOf("Rock", "Bug"),
        "feebas" to listOf("Water"), "milotic" to listOf("Water"),
        "castform" to listOf("Normal"),
        "kecleon" to listOf("Normal"),
        "shuppet" to listOf("Ghost"), "banette" to listOf("Ghost"),
        "duskull" to listOf("Ghost"), "dusclops" to listOf("Ghost"),
        "tropius" to listOf("Grass", "Flying"),
        "chimecho" to listOf("Psychic"),
        "absol" to listOf("Dark"),
        "wynaut" to listOf("Psychic"),
        "snorunt" to listOf("Ice"), "glalie" to listOf("Ice"),
        "spheal" to listOf("Ice", "Water"), "sealeo" to listOf("Ice", "Water"), "walrein" to listOf("Ice", "Water"),
        "clamperl" to listOf("Water"), "huntail" to listOf("Water"), "gorebyss" to listOf("Water"),
        "relicanth" to listOf("Water", "Rock"),
        "luvdisc" to listOf("Water"),
        "bagon" to listOf("Dragon"), "shelgon" to listOf("Dragon"), "salamence" to listOf("Dragon", "Flying"),
        "beldum" to listOf("Steel", "Psychic"), "metang" to listOf("Steel", "Psychic"), "metagross" to listOf("Steel", "Psychic"),
        "regirock" to listOf("Rock"), "regice" to listOf("Ice"), "registeel" to listOf("Steel"),
        "latias" to listOf("Dragon", "Psychic"), "latios" to listOf("Dragon", "Psychic"),
        "kyogre" to listOf("Water"), "groudon" to listOf("Ground"), "rayquaza" to listOf("Dragon", "Flying"),
        "jirachi" to listOf("Steel", "Psychic"), "deoxys" to listOf("Psychic"),

        // Gen 4-9 Modern Aces & Competitive Format Favorites
        "staraptor" to listOf("Normal", "Flying"), "luxray" to listOf("Electric"), "roserade" to listOf("Grass", "Poison"),
        "garchomp" to listOf("Dragon", "Ground"), "lucario" to listOf("Fighting", "Steel"), "hippowdon" to listOf("Ground"),
        "drapion" to listOf("Poison", "Dark"), "toxicroak" to listOf("Poison", "Fighting"), "abomasnow" to listOf("Grass", "Ice"),
        "weavile" to listOf("Dark", "Ice"), "magnezone" to listOf("Electric", "Steel"), "rhyperior" to listOf("Ground", "Rock"),
        "tangrowth" to listOf("Grass"), "electivire" to listOf("Electric"), "magmortar" to listOf("Fire"),
        "togekiss" to listOf("Fairy", "Flying"), "yanmega" to listOf("Bug", "Flying"), "gliscor" to listOf("Ground", "Flying"),
        "mamoswine" to listOf("Ice", "Ground"), "porygonz" to listOf("Normal"), "dusknoir" to listOf("Ghost"),
        "froslass" to listOf("Ice", "Ghost"), "rotom" to listOf("Electric", "Ghost"), "rotomwash" to listOf("Electric", "Water"),
        "rotomheat" to listOf("Electric", "Fire"), "rotommow" to listOf("Electric", "Grass"), "rotomfrost" to listOf("Electric", "Ice"), "rotomfan" to listOf("Electric", "Flying"),
        "uxie" to listOf("Psychic"), "mesprit" to listOf("Psychic"), "azelf" to listOf("Psychic"),
        "dialga" to listOf("Steel", "Dragon"), "palkia" to listOf("Water", "Dragon"), "heatran" to listOf("Fire", "Steel"),
        "regigigas" to listOf("Normal"), "giratina" to listOf("Ghost", "Dragon"), "cresselia" to listOf("Psychic"),
        "darkrai" to listOf("Dark"), "shaymin" to listOf("Grass"), "arceus" to listOf("Normal"),

        // Gen 5-9 High-Usage Meta
        "victini" to listOf("Psychic", "Fire"), "serperior" to listOf("Grass"), "emboar" to listOf("Fire", "Fighting"),
        "samurott" to listOf("Water"), "excadrill" to listOf("Ground", "Steel"), "conkeldurr" to listOf("Fighting"),
        "scolipede" to listOf("Bug", "Poison"), "whimsicott" to listOf("Grass", "Fairy"), "krookodile" to listOf("Ground", "Dark"),
        "darmanitan" to listOf("Fire"), "crustle" to listOf("Bug", "Rock"), "scrafty" to listOf("Dark", "Fighting"),
        "zoroark" to listOf("Dark"), "zoroarkhisui" to listOf("Normal", "Ghost"), "reuniclus" to listOf("Psychic"),
        "ferrothorn" to listOf("Grass", "Steel"), "chandelure" to listOf("Ghost", "Fire"), "haxorus" to listOf("Dragon"),
        "mienshao" to listOf("Fighting"), "hydreigon" to listOf("Dark", "Dragon"), "volcarona" to listOf("Bug", "Fire"),
        "cobalion" to listOf("Steel", "Fighting"), "terrakion" to listOf("Rock", "Fighting"), "virizion" to listOf("Grass", "Fighting"),
        "tornadus" to listOf("Flying"), "thundurus" to listOf("Electric", "Flying"), "landorus" to listOf("Ground", "Flying"), "landorustherian" to listOf("Ground", "Flying"),
        "kyurem" to listOf("Dragon", "Ice"), "keldeo" to listOf("Water", "Fighting"),

        // Gen 6-9 Modern Meta & Paradox
        "chesnaught" to listOf("Grass", "Fighting"), "delphox" to listOf("Fire", "Psychic"), "greninja" to listOf("Water", "Dark"),
        "talonflame" to listOf("Fire", "Flying"), "aegislash" to listOf("Steel", "Ghost"), "sylveon" to listOf("Fairy"),
        "goodra" to listOf("Dragon"), "goodrahisui" to listOf("Steel", "Dragon"), "hawlucha" to listOf("Fighting", "Flying"),
        "diancie" to listOf("Rock", "Fairy"), "hoopa" to listOf("Psychic", "Ghost"), "volcanion" to listOf("Fire", "Water"),

        // Gen 7-9 Ultra & Galar
        "decidueye" to listOf("Grass", "Ghost"), "incineroar" to listOf("Fire", "Dark"), "primarina" to listOf("Water", "Fairy"),
        "vikavolt" to listOf("Bug", "Electric"), "toxapex" to listOf("Poison", "Water"), "mudsdale" to listOf("Ground"),
        "bewear" to listOf("Normal", "Fighting"), "mimikyu" to listOf("Ghost", "Fairy"), "dhelmise" to listOf("Ghost", "Grass"),
        "kommoo" to listOf("Dragon", "Fighting"), "tapukoko" to listOf("Electric", "Fairy"), "tapulele" to listOf("Psychic", "Fairy"),
        "tapubulu" to listOf("Grass", "Fairy"), "tapufini" to listOf("Water", "Fairy"), "solgaleo" to listOf("Psychic", "Steel"),
        "lunala" to listOf("Psychic", "Ghost"), "nihilego" to listOf("Rock", "Poison"), "buzzwole" to listOf("Bug", "Fighting"),
        "pheromosa" to listOf("Bug", "Fighting"), "xurkitree" to listOf("Electric"), "celesteela" to listOf("Steel", "Flying"),
        "kartana" to listOf("Grass", "Steel"), "guzzlord" to listOf("Dark", "Dragon"), "necrozma" to listOf("Psychic"),
        "magearna" to listOf("Steel", "Fairy"), "marshadow" to listOf("Fighting", "Ghost"), "zeraora" to listOf("Electric"),

        // Gen 8 Galar & Hisui
        "rillaboom" to listOf("Grass"), "cinderace" to listOf("Fire"), "inteleon" to listOf("Water"),
        "corviknight" to listOf("Flying", "Steel"), "orbeetle" to listOf("Bug", "Psychic"), "drednaw" to listOf("Water", "Rock"),
        "coalossal" to listOf("Rock", "Fire"), "flapple" to listOf("Grass", "Dragon"), "appletun" to listOf("Grass", "Dragon"),
        "toxtricity" to listOf("Electric", "Poison"), "centiskorch" to listOf("Fire", "Bug"), "hatterene" to listOf("Psychic", "Fairy"),
        "grimmsnarl" to listOf("Dark", "Fairy"), "obstagoon" to listOf("Dark", "Normal"), "perrserker" to listOf("Steel"),
        "sirfetchd" to listOf("Fighting"), "mrrime" to listOf("Ice", "Psychic"), "runerigus" to listOf("Ground", "Ghost"),
        "falinks" to listOf("Fighting"), "pincurchin" to listOf("Electric"), "frosmoth" to listOf("Ice", "Bug"),
        "stonjourner" to listOf("Rock"), "eiscue" to listOf("Ice"), "indeedee" to listOf("Psychic", "Normal"),
        "copperajah" to listOf("Steel"), "dracozolt" to listOf("Electric", "Dragon"), "arctozolt" to listOf("Electric", "Ice"),
        "dracovish" to listOf("Water", "Dragon"), "arctovish" to listOf("Water", "Ice"), "duraludon" to listOf("Steel", "Dragon"),
        "dragapult" to listOf("Dragon", "Ghost"), "zacian" to listOf("Fairy"), "zamazenta" to listOf("Fighting"),
        "eternatus" to listOf("Poison", "Dragon"), "urshifu" to listOf("Fighting", "Dark"), "urshifurapidstrike" to listOf("Fighting", "Water"),
        "regieleki" to listOf("Electric"), "regidrago" to listOf("Dragon"), "calyrex" to listOf("Psychic", "Grass"),
        "wyrdeer" to listOf("Normal", "Psychic"), "kleavor" to listOf("Bug", "Rock"), "ursaluna" to listOf("Ground", "Normal"),
        "basculegion" to listOf("Water", "Ghost"), "sneasler" to listOf("Fighting", "Poison"), "overqwil" to listOf("Dark", "Poison"),
        "enamorus" to listOf("Fairy", "Flying"),

        // Gen 9 Paldea & Paradox
        "meowscarada" to listOf("Grass", "Dark"), "skeledirge" to listOf("Fire", "Ghost"), "quaquaval" to listOf("Water", "Fighting"),
        "oinkologne" to listOf("Normal"), "lokix" to listOf("Bug", "Dark"), "pawmot" to listOf("Electric", "Fighting"),
        "maushold" to listOf("Normal"), "dachsbun" to listOf("Fairy"), "arboliva" to listOf("Grass", "Normal"),
        "squawkabilly" to listOf("Normal", "Flying"), "garganacl" to listOf("Rock"), "armarouge" to listOf("Fire", "Psychic"),
        "ceruledge" to listOf("Fire", "Ghost"), "bellibolt" to listOf("Electric"), "kilowattrel" to listOf("Electric", "Flying"),
        "mabosstiff" to listOf("Dark"), "grafaiai" to listOf("Poison", "Normal"), "brambleghast" to listOf("Grass", "Ghost"),
        "toedscruel" to listOf("Ground", "Grass"), "klawf" to listOf("Rock"), "scovillain" to listOf("Grass", "Fire"),
        "rabsca" to listOf("Bug", "Psychic"), "espathra" to listOf("Psychic"), "tinkaton" to listOf("Fairy", "Steel"),
        "wugtrio" to listOf("Water"), "bombirdier" to listOf("Dark", "Flying"), "palafin" to listOf("Water"),
        "revavroom" to listOf("Steel", "Poison"), "cyclizar" to listOf("Dragon", "Normal"), "orthworm" to listOf("Steel"),
        "glimmora" to listOf("Rock", "Poison"), "houndstone" to listOf("Ghost"), "flamigo" to listOf("Flying", "Fighting"),
        "cetitan" to listOf("Ice"), "veluza" to listOf("Water", "Psychic"), "dondozo" to listOf("Water"),
        "tatsugiri" to listOf("Dragon", "Water"), "annihilape" to listOf("Fighting", "Ghost"), "clodsire" to listOf("Poison", "Ground"),
        "farigiraf" to listOf("Normal", "Psychic"), "dudunsparce" to listOf("Normal"), "kingambit" to listOf("Dark", "Steel"),
        "greattusk" to listOf("Ground", "Fighting"), "screamtail" to listOf("Fairy", "Psychic"), "brutebonnet" to listOf("Grass", "Dark"),
        "fluttermane" to listOf("Ghost", "Fairy"), "slitherwing" to listOf("Bug", "Fighting"), "sandyshocks" to listOf("Electric", "Ground"),
        "irontreads" to listOf("Ground", "Steel"), "ironbundle" to listOf("Ice", "Water"), "ironhands" to listOf("Fighting", "Electric"),
        "ironjugulis" to listOf("Dark", "Flying"), "ironmoth" to listOf("Fire", "Poison"), "ironthorns" to listOf("Rock", "Electric"),
        "baxcalibur" to listOf("Dragon", "Ice"), "gholdengo" to listOf("Steel", "Ghost"), "wochien" to listOf("Dark", "Grass"),
        "chienpao" to listOf("Dark", "Ice"), "tinglu" to listOf("Dark", "Ground"), "yuyu" to listOf("Dark", "Fire"),
        "roaringmoon" to listOf("Dragon", "Dark"), "ironvaliant" to listOf("Fairy", "Fighting"), "koraidon" to listOf("Fighting", "Dragon"),
        "miraidon" to listOf("Electric", "Dragon"), "walkingwake" to listOf("Water", "Dragon"), "ironleaves" to listOf("Grass", "Psychic"),
        "dipplin" to listOf("Grass", "Dragon"), "poltchageist" to listOf("Grass", "Ghost"), "sinistcha" to listOf("Grass", "Ghost"),
        "okidogi" to listOf("Poison", "Fighting"), "munkidori" to listOf("Poison", "Psychic"), "fezandipiti" to listOf("Poison", "Fairy"),
        "ogerpon" to listOf("Grass"), "ogerponwellspring" to listOf("Grass", "Water"), "ogerponhearthflame" to listOf("Grass", "Fire"), "ogerponcornerstone" to listOf("Grass", "Rock"),
        "archaludon" to listOf("Steel", "Dragon"), "hydrapple" to listOf("Grass", "Dragon"), "ironboulder" to listOf("Rock", "Psychic"),
        "ironcrown" to listOf("Steel", "Psychic"), "gougingfire" to listOf("Fire", "Dragon"), "ragingbolt" to listOf("Electric", "Dragon"),
        "terapagos" to listOf("Normal"), "pecharunt" to listOf("Poison", "Ghost")
    )

    fun getTypesForSpecies(speciesName: String): List<String> {
        if (speciesName.isBlank()) return listOf("Normal")

        // Strip position prefix like "p1a: ", "p2a: ", "p1: ", etc. and details like ", L80, M"
        val cleanName = speciesName
            .split(":").last()
            .split(",").first()
            .trim()

        val cleanKey = cleanName.lowercase().replace(Regex("[^a-z0-9]"), "")

        return dynamicSpeciesTypes[cleanKey] ?: speciesTypes[cleanKey] ?: inferTypeFromName(cleanKey)
    }

    private fun inferTypeFromName(cleanName: String): List<String> {
        return when {
            cleanName.contains("dragon") -> listOf("Dragon")
            cleanName.contains("fire") || cleanName.contains("flame") -> listOf("Fire")
            cleanName.contains("water") || cleanName.contains("aqua") -> listOf("Water")
            cleanName.contains("grass") || cleanName.contains("leaf") -> listOf("Grass")
            cleanName.contains("electric") || cleanName.contains("thunder") || cleanName.contains("volt") -> listOf("Electric")
            cleanName.contains("ice") || cleanName.contains("frost") -> listOf("Ice")
            cleanName.contains("dark") || cleanName.contains("shadow") -> listOf("Dark")
            cleanName.contains("iron") || cleanName.contains("steel") -> listOf("Steel")
            cleanName.contains("ghost") || cleanName.contains("specter") -> listOf("Ghost")
            cleanName.contains("fairy") || cleanName.contains("pixie") -> listOf("Fairy")
            cleanName.contains("rock") || cleanName.contains("stone") -> listOf("Rock")
            cleanName.contains("ground") || cleanName.contains("earth") -> listOf("Ground")
            cleanName.contains("bug") -> listOf("Bug")
            cleanName.contains("poison") || cleanName.contains("toxic") -> listOf("Poison")
            cleanName.contains("psychic") || cleanName.contains("psi") -> listOf("Psychic")
            cleanName.contains("fly") || cleanName.contains("wing") || cleanName.contains("bird") -> listOf("Flying")
            cleanName.contains("fight") || cleanName.contains("punch") -> listOf("Fighting")
            else -> listOf("Normal")
        }
    }
}
