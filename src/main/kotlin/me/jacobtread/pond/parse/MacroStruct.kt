package me.jacobtread.pond.parse

import me.jacobtread.pond.instr.Instruction

class MacroStruct(
    val name: String,
    val arguments: List<String>,
    private val children: List<Token>
) {

    fun compile(parent: TokenConsumer, values: HashMap<String, String>): List<Instruction> {
        val variables: HashMap<String, String> = HashMap(values)
        println(variables)
        val parser = PondParser(object : TokenConsumer() {
            override fun variable(name: String): String {
                return if (name in variables) {
                    variables[name]!!
                } else parent.variable(name)
            }

            override fun variableSet(name: String): Boolean {
                return name in variables || parent.variableSet(name)
            }

        })
        parser.parse(children)
        return parser.result()
    }

}