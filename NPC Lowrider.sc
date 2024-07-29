// You need: https://forum.mixmods.com.br/f141-gta3script-cleo/t5206-como-criar-scripts-com-cleoplus
SCRIPT_START
{
	LVAR_INT scplayer
	LVAR_INT char hVeh randomtime iMinDelay iMaxDelay iFindProgress i
	LVAR_INT hasBlip//test
	LVAR_FLOAT X1 Y1 X2 Y2 Z wheel_fl wheel_bl wheel_fr wheel_br speed fMaxSpeed radiusSearch

	IF NOT READ_FLOAT_FROM_INI_FILE "CLEO\NPC Lowrider.ini" "Settings" "MaxSpeed" (fMaxSpeed)
		fMaxSpeed = 15.0
		PRINT_STRING_NOW "NPC Lowrider.ini not founded." 3000
	ENDIF
	IF NOT READ_INT_FROM_INI_FILE "CLEO\NPC Lowrider.ini" "Settings" "MinDelay" (iMinDelay)
		iMinDelay = 300
		PRINT_STRING_NOW "NPC Lowrider.ini not founded." 3000
	ENDIF
	IF NOT READ_INT_FROM_INI_FILE "CLEO\NPC Lowrider.ini" "Settings" "MaxDelay" (iMaxDelay)
		iMaxDelay = 2000
		PRINT_STRING_NOW "NPC Lowrider.ini not founded." 3000
	ENDIF

	GET_PLAYER_CHAR 0 scplayer

	WHILE TRUE
		WAIT 0
		iFindProgress = 0
		WHILE GET_ANY_CAR_NO_SAVE_RECURSIVE iFindProgress (iFindProgress hVeh)
			GOSUB ProcessCar
		ENDWHILE
	ENDWHILE

	// We can't process this on car create event, because we need to check driver and if the car is script controlled
	ProcessCar:
	// Note that we don't even manipulate the value, just the vars existence is enough in this case
/*	IF GET_EXTENDED_CAR_VAR hVeh AUTO 1 (i)
		RETURN
	ELSE
		INIT_EXTENDED_CAR_VARS hVeh AUTO 1
	ENDIF
*/
	GET_CHAR_COORDINATES scplayer X1 Y1 Z
	GET_CHAR_COORDINATES scplayer X2 Y2 Z
	radiusSearch = 45.0
	X1 -= radiusSearch
	Y1 -= radiusSearch
	X2 += radiusSearch
	Y2 += radiusSearch

	IF NOT IS_CAR_IN_AREA_2D hVeh X1 Y1 X2 Y2 FALSE
		RETURN
	ENDIF

	// Only young drivers
	GET_DRIVER_OF_CAR hVeh (char)

	IF NOT char > 0
/*		GET_CHAR_STAT_ID char (i)
		IF i > PEDSTAT_GANG10
			IF i = PEDSTAT_STREET_GUY
			OR i = PEDSTAT_STREET_GIRL
			OR i = PEDSTAT_GEEK_GIRL
			OR i = PEDSTAT_GEEK_GUY
			OR i = PEDSTAT_CRIMINAL
			OR i = PEDSTAT_BEACH_GUY
			OR i = PEDSTAT_BEACH_GIRL
				// Add exception for some peds, because there is no sense
				IF IS_CHAR_MODEL j BFOST
					RETURN
				ENDIF
			ELSE
				RETURN
			ENDIF
		ENDIF
*/		RETURN
	ENDIF

	IF IS_CAR_OWNED_BY_PLAYER hVeh
		RETURN
	ENDIF
/*
	GET_CITY_PLAYER_IS_IN 0 (i)
	IF i = 0 //countryside
	OR i = 4 //desert
		bIsOffCity = TRUE
	ELSE
		bIsOffCity = FALSE
	ENDIF
*/
//	GOSUB search
	IF DOES_VEHICLE_EXIST hVeh
		IF DOES_CAR_HAVE_HYDRAULICS hVeh
			//How to apply physics here?
			GET_DRIVER_OF_CAR hVeh char
			IF IS_CAR_ON_SCREEN hVeh //This is just because I don't know how apply physics.
				WHILE LOCATE_CHAR_DISTANCE_TO_CAR scplayer hVeh 100.0
				AND NOT IS_CAR_DEAD hVeh
				AND DOES_CHAR_EXIST char
					WAIT 0
					GET_CAR_SPEED hVeh speed
					IF IS_CHAR_ON_FOOT char
					OR IS_CHAR_DEAD char
					OR IS_CAR_UPSIDEDOWN hVeh
						BREAK
					ENDIF
					IF speed > fMaxSpeed	//Max speed to bounce
						CONTROL_CAR_HYDRAULICS hVeh 0.0 0.0 0.0 0.0
						WAIT 0
						BREAK
					ENDIF
					IF timera > randomtime
						GENERATE_RANDOM_FLOAT_IN_RANGE 0.0 2.0 wheel_fl
						GENERATE_RANDOM_FLOAT_IN_RANGE 0.0 2.0 wheel_bl
						GENERATE_RANDOM_FLOAT_IN_RANGE 0.0 2.0 wheel_fr
						GENERATE_RANDOM_FLOAT_IN_RANGE 0.0 2.0 wheel_br

						IF wheel_fl > 1.0
							wheel_fl = 1.0
						ELSE
							wheel_fl = 0.0
						ENDIF
						IF wheel_bl > 1.0
							wheel_bl = 1.0
						ELSE
							wheel_bl = 0.0
						ENDIF
						IF wheel_fr > 1.0
							wheel_fr = 1.0
						ELSE
							wheel_fr = 0.0
						ENDIF
						IF wheel_br > 1.0
							wheel_br = 1.0
						ELSE
							wheel_br = 0.0
						ENDIF
						timera = 0
						GENERATE_RANDOM_INT_IN_RANGE iMinDelay iMaxDelay randomtime	//300 to 2000 like lowr.sc
					ELSE
						CONTROL_CAR_HYDRAULICS hVeh wheel_fl wheel_bl wheel_fr wheel_br
						IF DOES_BLIP_EXIST hasBlip//test
						ELSE//test
							ADD_BLIP_FOR_CAR hVeh hasBlip//test
						ENDIF//test
					ENDIF
				ENDWHILE
				GOSUB forget
			ENDIF
			GOSUB forget
		ENDIF
		GOSUB forget
	ENDIF

	RETURN

	forget:
	MARK_CHAR_AS_NO_LONGER_NEEDED char
//	MARK_CAR_AS_NO_LONGER_NEEDED hVeh
	REMOVE_BLIP hasBlip//test
	RETURN

}
SCRIPT_END