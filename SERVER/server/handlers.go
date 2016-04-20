package cs3235

import (
	"appengine"
	"appengine/datastore"
	"encoding/json"
	"fmt"
	"net/http"
)

func Index(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintln(w, "Welcome to cs3235-2!")
}

/*
Test with comand:
	curl -H "Content-Type: application/json" -X POST -d "{\"psehex\":\"hex1234\", \"pse\":\"1234\", \"readresulthex\":\"resulthex\", \"readresult\":\"result\"}" http://localhost:8080/cardinfo
	curl -H "Content-Type: application/json" -X POST -d "{\"psehex\":\"hex1234\", \"pse\":\"1234\", \"readresulthex\":\"resulthex\", \"readresult\":\"result\"}" http://cs3235-2.appspot.com/cardinfo
*/
func PostCardInfo(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Access-Control-Allow-Origin", "*")
	ctx := appengine.NewContext(r)
	var cardInfo CardInfo

	decoder := json.NewDecoder(r.Body)
	err := decoder.Decode(&cardInfo)
	if err != nil {
		panic(err)
	}

	fmt.Fprintln(w, cardInfo)

	key, err := datastore.Put(ctx, datastore.NewIncompleteKey(ctx, "card", nil), &cardInfo)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Fprintln(w, key)
}

func OptionsCardInfo(w http.ResponseWriter, r *http.Request) {
	// Needed?
	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("Access-Control-Allow-Methods", "POST")
	w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
}
