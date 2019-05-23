(ns cardtooltip.web
   (:require
    [clojure.java.io :as io]
    [clojure.data.json :as json]
    [hiccup.page :as h]
    [compojure.core :refer [defroutes GET POST ANY context]]
    [compojure.route :refer [not-found resources]]
    [clj-http.client :as http]
    [ring.util.response :refer [response resource-response content-type redirect]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]
    [ring.middleware.session :refer [wrap-session]]
    [cardtooltip.tools :as tools]))
			
(def decks ["_wED9DexgA==" "_wEB1ARxhwHFBvmOAcaZQJQDAQwWlwMwMXSl"])

(def header
  [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport" :content "width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"}]
      [:meta {:http-equiv "X-UA-Compatible" :content "ie=edge"}]
      [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.js"}]
      [:link {:href "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" :rel "stylesheet" :integrity "sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" :crossorigin "anonymous"}]
      [:script {:src "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" :integrity "sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" :crossorigin "anonymous"}]
      [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/taffydb/2.7.3/taffy-min.js" :integrity "sha256-fKCEY8Tw1ywvNmNo7LXWhLLCkh+AP8ABrNR5S3SmSvs=" :crossorigin "anonymous"}]
      [:script {:src "/js/externs/warhammer-card-tooltip.min.js"}]
      (h/include-js "/js/externs/warhammer-card-tooltip.min.js")
      (h/include-css "/css/style.css")])
      
(def navbar
  [:nav.navbar.navbar-dark.bg-dark.navbar-expand-lg
    [:button.navbar-toggler {:type "button" :data-toggle "collapse" :data-target "#navbarNavDropdown"}
      [:span.navbar-toggler-icon]]
    [:div#navbarNavDropdown.collapse.navbar-collapse
      [:ul.navbar-nav
        [:li.nav-item [:a.nav-link {:href "/"} "Home"]]
        [:li.nav-item [:a.nav-link {:href "/index.html"} "Basic Styling"]]
        [:li.nav-item [:a.nav-link {:href "/self-style.html"} "Self Styling"]]
        [:li.nav-item [:a.nav-link {:href "/parse"} "Parse Deck"]]
        [:li.nav-item [:a.nav-link {:href "/local/carddata"} "local data"]]
        [:li.nav-item [:a.nav-link {:href "/source/carddata"} "source data"]]
        [:li.nav-item [:a.nav-link {:href "/source/customsource"} "Custom source URL"]]
      ]]])
    
			
(defn- parse-handler [req]
  (h/html5
    header
    [:body
      navbar
      [:div.container 
        (for [deck decks]
          [:div 
            [:div deck]
            [:div (-> deck tools/parsedeck str)]
          ])]]))

(defn- carddatabase-handler [req]
  (h/html5
    (into header
      (h/include-js "/js/carddatabase.js"))
    [:body
      navbar
      [:div.container
        [:div.row.my-2
          [:input#filter.form-control]]
        [:div.row
          [:table#cardtable.table.table-sm.table-hover
            [:thead
              [:tr
                [:th {:scope "col"} "#"]
                [:th {:scope "col"} "Collector #"]
                [:th.card-tooltip {:scope "col"} "Name"]
                [:th {:scope "col"} "Category"]
                [:th {:scope "col"} "Alliance"]
                [:th {:scope "col"} "Class"]  
                [:th {:scope "col"} "Corners"]
                [:th {:scope "col"} "Wave"]
                ]]
            [:tbody#tblbody]]]]]))
            
(defn getsearch [ size ]
    (http/post "https://carddatabase.warhammerchampions.com/warhammer-cards/_search" 
               {:content-type :json
                :body (json/write-str {:size size :from 1})}))

(defn cardcount []
  (-> (getsearch 1)
      :body 
      (json/read-str :key-fn keyword)
      :hits
      :total))
      
(defn getsearchurl [ url ]
  (http/get url))
    
(defn- custom-source-handler [req]
  (h/html5
    header
    [:body
      navbar
      [:div.container
        [:form {:method "post" :action "/source/customsource"}
          [:div.form-group
            [:label {:for "#url"} "Source URL"]
            [:input.form-control {:type "text" :placeholder "http://" :name "url"}]]
          [:button.btn.btn-primary {:type "submit"} "Go"]]
				[:a.btn.btn-primary {:href "/source/customsource/lotrscenarios"} "LotR Scenarios"]]]))

(defn- lotrscenarios []
	(map #(
		(http/get (str "http://ringsdb.com/api/public/scenario/" %))
	) (range 1 107)))
				
(defroutes app-routes
  (GET "/" req
    carddatabase-handler)
  (GET "/test" [] (h/html5 header [:body navbar]))
  (GET "/parse" []
    parse-handler)
  (GET "/local/carddata" []
    (-> (io/resource "private/carddatabase.json")
        slurp
        response
        (content-type "application/json")))
  (GET "/source/carddata" []
    (-> (getsearch (cardcount))
        :body
        response
        (content-type "application/json")))        
  (GET "/source/customsource" []
    custom-source-handler)
	(GET "/source/customsource/lotrscenarios" []
		(-> (lotrscenarios)
				:body
				response
				(content-type "application/json")))
  (POST "/source/customsource" [url]
    (-> (getsearchurl url)
        :body
        response
        (content-type "application/json")))
  (resources "/"))
   
(def app 
  (-> app-routes
    (wrap-keyword-params)
    (wrap-params)
    (wrap-session)))
   