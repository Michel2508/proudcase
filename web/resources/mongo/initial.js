/* 
 * This script adds automaticly categories and languages to the
 * mongo database. 
 * 
 * Currently supported languages:
 * English
 * German
 */

db = db.getSiblingDB('proudcase')

db.CategorieBean.insert( [
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Video",
                language : "de"
            },
            {
                categoriename : "Video",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Musik",
                language : "de"
            },
            {
                categoriename : "Music",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Event",
                language : "de"
            },
            {
                categoriename : "Event",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Theater",
                language : "de"
            },
            {
                categoriename : "Theater",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Literatur",
                language : "de"
            },
            {
                categoriename : "Literature",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Kunst",
                language : "de"
            },
            {
                categoriename : "Art",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Fotografie",
                language : "de"
            },
            {
                categoriename : "Photography",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Erfindung",
                language : "de"
            },
            {
                categoriename : "Invention",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Journalismus",
                language : "de"
            },
            {
                categoriename : "Journalism",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Design",
                language : "de"
            },
            {
                categoriename : "Design",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Kulturelle Bildung",
                language : "de"
            },
            {
                categoriename : "Cultural education",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Spiele",
                language : "de"
            },
            {
                categoriename : "Games",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Mode",
                language : "de"
            },
            {
                categoriename : "Fashion",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Hörbuch",
                language : "de"
            },
            {
                categoriename : "Audio drama",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Informationsangebot",
                language : "de"
            },
            {
                categoriename : "Information service",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Technologie",
                language : "de"
            },
            {
                categoriename : "Technology",
                language : "en"
               
            }
        ]
    },
    {
        className : "com.proudcase.persistence.CategorieBean",
        langCategorieList : [ 
            {
                categoriename : "Comic",
                language : "de"
            },
            {
                categoriename : "Comic",
                language : "en"
               
            }
        ]
    }
] );


db.SupportedLanguagesBean.insert( [
    {
        className : "com.proudcase.persistence.SupportedLanguagesBean",
        language : "de"
    },
    {
        className : "com.proudcase.persistence.SupportedLanguagesBean",
        language : "en"
    }
]);
