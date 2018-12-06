(ns cardtooltip.web
   (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [hiccup.page :as h]
            [compojure.core :refer [defroutes GET POST ANY context]]
            [compojure.route :refer [not-found resources]]
            [clj-http.client :as client]
            [ring.util.response :refer [response resource-response content-type redirect]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.session :refer [wrap-session]]))

(defn- carddatabase-handler [req]
  (h/html5
    [:head 
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport" :content "width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"}]
      [:meta {:http-equiv "X-UA-Compatible" :content "ie=edge"}]
      [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.js"}]
      [:link {:href "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" :rel "stylesheet" :integrity "sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" :crossorigin "anonymous"}]
      [:script {:src "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" :integrity "sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" :crossorigin "anonymous"}]
      [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/taffydb/2.7.3/taffy-min.js" :integrity "sha256-fKCEY8Tw1ywvNmNo7LXWhLLCkh+AP8ABrNR5S3SmSvs=" :crossorigin "anonymous"}]
      [:script {:src "/js/externs/warhammer-card-tooltip.min.js"}]
      [:script {:src "/js/carddatabase.js"}]
      (h/include-css "/css/style.css")
      ]
    [:body
      [:div.container.my-3
        [:div.row.mb-2
          [:form 
            [:input#filter.form-control]]]
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

(defroutes app-routes
  (GET "/" req
    carddatabase-handler)
  (GET "/home" []
    (response (slurp (io/resource "public/home.html"))))
  (GET "/api/carddatabase" []
    (-> (io/resource "private/carddatabase.json")
        slurp
        response
        (content-type "application/json")))
  ;;(GET "/_mapping" []
  ;;  (try 
  ;;    (client/get "https://carddatabase.warhammerchampions.com/warhammer-cards/_mapping" {:insecure? true})
  ;;    (catch Exception e (prn e))))
  ;;(GET "/_search" []
  ;;  (try
  ;;    (client/post "https://carddatabase.warhammerchampions.com/warhammer-cards/_mapping" {
  ;;      :content-type :json
  ;;      :body (json/write-str {})
  ;;    })
  ;;    (catch Exception e (prn e))))
  (resources "/"))
   
(def app 
  (-> app-routes
    (wrap-keyword-params)
    (wrap-params)
    (wrap-session)))
   