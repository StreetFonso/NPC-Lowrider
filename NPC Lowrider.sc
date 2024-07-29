SCRIPT_START
{
	LVAR_INT scplayer
	LVAR_INT char hVeh randomtime iMinDelay iMaxDelay
//	LVAR_INT hasBlip//test
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
/*
	main://This label is to the others scripts don't inicialize at the same time.
	WAIT 1
*/
	main_loop:
	WAIT 0
	GET_CHAR_COORDINATES scplayer X1 Y1 Z
	GET_CHAR_COORDINATES scplayer X2 Y2 Z
	GOSUB search
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
//						IF DOES_BLIP_EXIST hasBlip//test
//						ELSE//test
//							ADD_BLIP_FOR_CAR hVeh hasBlip//test
//						ENDIF//test
					ENDIF
				ENDWHILE
				GOSUB forget
			ENDIF
			GOSUB forget
		ENDIF
		GOSUB forget
	ENDIF

	GOTO main_loop

	search:
	GET_RANDOM_CAR_OF_TYPE_IN_AREA X1 Y1 X2 Y2 -1 hVeh
	IF DOES_VEHICLE_EXIST hVeh
		RETURN
	ENDIF
	IF radiusSearch > 45.0
		radiusSearch = -45.0
		RETURN //In case of teleportation, this will get the actual positon after reach limit of radius search.
	ELSE
		radiusSearch += 1.0
	ENDIF
	X1 -= radiusSearch
	Y1 -= radiusSearch
	X2 += radiusSearch
	Y2 += radiusSearch
	GOTO search
	RETURN

	forget:
	MARK_CHAR_AS_NO_LONGER_NEEDED char
	MARK_CAR_AS_NO_LONGER_NEEDED hVeh //Isso precisa de uma solução melhor, pois faz o veículo do Cidinei sumir ao afastar-se.
//	REMOVE_BLIP hasBlip//test
	RETURN
}

SCRIPT_END
