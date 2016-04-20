package cs3235

import (
	"net/http"
)

func init() {
	router := NewRouter()
	http.Handle("/", router)
}
