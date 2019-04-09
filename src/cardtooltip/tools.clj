(ns cardtooltip.tools
  (:require
    [clojure.string :as str]
    [octet.core :as buf]))
	

      
;;;;;;;;;;;;; Parse Deck ;;;;;;;;;;;;;;

(import java.util.Base64)

(def MAX_SUPPORTED_VERSION 1)
(def ^:const BYTES_PER_CARD 5)
(def wh-spec (buf/spec :count buf/byte :id buf/int32))

; RYO instad of octet
;
;(defn toUint32 [ta]
;  (bit-or (bit-shift-left (aget ta 3) 24)
;          (bit-shift-left (bit-and 0xff (aget ta 2)) 16)
;          (bit-shift-left (bit-and 0xff (aget ta 1)) 8)
;          (bit-and 0xff (aget ta 0))))
;(defn getCards [data]
;  (let [cardData (-> data second byte-array)]
;    (prn cardData)
;    (for [i (range 0 (count cardData) 5)]
;      (assoc {} :count (aget cardData i) 
;                :id (-> cardData (nthrest (inc i)) byte-array toUint32x)))))

(defn- fromBase64 [b64] 
  (.decode (Base64/getDecoder) b64))
  
(defn- parseQueryStringDeckCode [qsDeckCode]
  (-> qsDeckCode
      (clojure.string/replace #"_" "/")
      (clojure.string/replace #"[- ]" "+")))

(defn- getVersionAndData [raw]
  (let [view (map int raw)]
    (if (= (and (first raw) 0xFF) 255)
        [(nth view 1) (nthrest view 2)]
        [0 []])))
        
(defn- getCards [cards]
  (let [n (count cards)
        buffer (buf/allocate n)]
    (buf/write! buffer (byte-array cards) (buf/repeat n buf/byte))
    (for [i (range 0 n BYTES_PER_CARD)]
      (buf/with-byte-order :little-endian
        (buf/read buffer wh-spec {:offset i})))))
        
(defn parsedeck [deckcode]
  (let [[version cards] (-> deckcode
                            parseQueryStringDeckCode
                            fromBase64
                            getVersionAndData)]
    {:version version :cards (getCards cards)}))
	