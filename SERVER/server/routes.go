package cs3235

import (
	"net/http"
)

type Route struct {
	Name        string
	Method      string
	Pattern     string
	HandlerFunc http.HandlerFunc
}

type Routes []Route

var routes = Routes{
	Route{
		"Welcome",
		"GET",
		"/",
		Index,
	},
	Route{
		"PostCardInfo",
		"POST",
		"/cardinfo",
		PostCardInfo,
	},
	Route{
		"OptionsCardInfo",
		"OPTIONS",
		"/cardinfo",
		OptionsCardInfo,
	},
}
