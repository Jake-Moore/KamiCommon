rootProject.name = "kamicommon"
include(":spigot-jar")
include(":spigot-utils")
include(":standalone-jar")
include(":standalone-utils")
include(":generic-jar")
include(":spigot-nms")

include(":spigot-nms:v1_8_R3")
include(":spigot-nms:v1_16_R1")
include(":spigot-nms:v1_17_R1")
include("spigot-nms:api")
findProject(":spigot-nms:api")?.name = "api"
include("spigot-nms:v1_8_R1")
findProject(":spigot-nms:v1_8_R1")?.name = "v1_8_R1"
include("spigot-nms:v1_8_R2")
findProject(":spigot-nms:v1_8_R2")?.name = "v1_8_R2"
include("spigot-nms:v1_9_R1")
findProject(":spigot-nms:v1_9_R1")?.name = "v1_9_R1"
include("spigot-nms:v1_9_R2")
findProject(":spigot-nms:v1_9_R2")?.name = "v1_9_R2"
include("spigot-nms:v1_10_R1")
findProject(":spigot-nms:v1_10_R1")?.name = "v1_10_R1"
include("spigot-nms:v1_11_R1")
findProject(":spigot-nms:v1_11_R1")?.name = "v1_11_R1"
include("spigot-nms:v1_12_R1")
findProject(":spigot-nms:v1_12_R1")?.name = "v1_12_R1"
include("spigot-nms:v1_13_R1")
findProject(":spigot-nms:v1_13_R1")?.name = "v1_13_R1"
include("spigot-nms:v1_13_R2")
findProject(":spigot-nms:v1_13_R2")?.name = "v1_13_R2"
include("spigot-nms:v1_14_R1")
findProject(":spigot-nms:v1_14_R1")?.name = "v1_14_R1"
include("spigot-nms:v1_15_R1")
findProject(":spigot-nms:v1_15_R1")?.name = "v1_15_R1"
include("spigot-nms:v1_16_R2")
findProject(":spigot-nms:v1_16_R2")?.name = "v1_16_R2"
include("spigot-nms:v1_16_R3")
findProject(":spigot-nms:v1_16_R3")?.name = "v1_16_R3"
include("spigot-nms:v1_18_R1")
findProject(":spigot-nms:v1_18_R1")?.name = "v1_18_R1"
include("spigot-nms:v1_18_R2")
findProject(":spigot-nms:v1_18_R2")?.name = "v1_18_R2"
include("spigot-nms:v1_19_R1")
findProject(":spigot-nms:v1_19_R1")?.name = "v1_19_R1"
include("spigot-nms:v1_19_R2")
findProject(":spigot-nms:v1_19_R2")?.name = "v1_19_R2"
include("spigot-nms:v1_19_R3")
findProject(":spigot-nms:v1_19_R3")?.name = "v1_19_R3"
include("spigot-nms:v1_20_R1")
findProject(":spigot-nms:v1_20_R1")?.name = "v1_20_R1"
include("spigot-nms:v1_20_R2")
findProject(":spigot-nms:v1_20_R2")?.name = "v1_20_R2"
include("spigot-nms:v1_20_R3")
findProject(":spigot-nms:v1_20_R3")?.name = "v1_20_R3"
