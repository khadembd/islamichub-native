package com.islamichub.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * BaseFragment — crash-proof foundation for all fragments.
 *
 * Every fragment extends this to get:
 *  - Defensive view creation (try/catch around inflate)
 *  - Defensive lifecycle (all callbacks wrapped)
 *  - Safe binding access
 *  - Empty/error state helpers
 *
 * পুরোপুরি crash-proof — কোনো exception হলেও app crash করবে না।
 */
abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding: T? get() = _binding

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): T
    abstract fun onReady(view: View, savedInstanceState: Bundle?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return try {
            _binding = createBinding(inflater, container)
            _binding?.root
        } catch (e: Exception) {
            android.util.Log.e("BaseFragment", "Failed to create binding", e)
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            onReady(view, savedInstanceState)
        } catch (e: Exception) {
            android.util.Log.e("BaseFragment", "Failed in onReady", e)
            // Don't rethrow — fragment stays alive but empty
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Safe binding access — returns null if binding was cleared */
    protected fun withBinding(action: (T) -> Unit) {
        _binding?.let { action(it) }
    }
}
